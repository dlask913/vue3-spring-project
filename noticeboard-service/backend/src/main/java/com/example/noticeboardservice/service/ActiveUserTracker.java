package com.example.noticeboardservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component @Slf4j
public class ActiveUserTracker {
    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> activeSessions = ConcurrentHashMap.newKeySet();

    public ActiveUserTracker(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 유저가 웹소켓에 연결했을 때
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String clientType = null;
        // 원본 CONNECT 메시지 프레임을 통째로 추출
        Message<?> connectMessage = (Message<?>) headerAccessor.getHeader("simpConnectMessage");
        if (connectMessage != null) {
            StompHeaderAccessor connectAccessor = StompHeaderAccessor.wrap(connectMessage);
            // nativeHeaders 안의 client-type 값 추출
            clientType = connectAccessor.getFirstNativeHeader("client-type");
        }
        log.info("[ConnectedEvent] 원본 메시지에서 추출한 client-type: {}", clientType);

        // SERVICE 세션만 카운트 & ID 중복 방지
        if ("SERVICE".equals(clientType) && sessionId != null) {
            activeSessions.add(sessionId);
        }

        int currentUsers = activeSessions.size();
        log.info("[연결] 세션 ID: {} | 현재 접속 세션 수: {}", sessionId, currentUsers);
        broadcastUserCount(currentUsers);
    }

    // 유저가 웹소켓 연결을 끊었을 때
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionId != null) {
            activeSessions.remove(sessionId); // 등록된 해당 세션만 제거
        }

        int currentUsers = activeSessions.size();
        log.info("[해제] 세션 ID: {} | 현재 접속 세션 수: {}", sessionId, currentUsers);
        broadcastUserCount(currentUsers);
    }

    // 유저가 구독(subscribe)을 마친 직후 그 유저 포함 전체에게 현재 카운트 전송
    @EventListener
    public void handleSessionSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if ("/topic/active-users".equals(headerAccessor.getDestination())) {
            broadcastUserCount(getActiveUserCount());
        }
    }

    // 구독 중인 클라이언트에게 현재 접속자 수 응답
    private void broadcastUserCount(int count) {
        messagingTemplate.convertAndSend("/topic/active-users", count);
    }

    // 현재 접속자 수 반환 메서드
    public int getActiveUserCount() {
        return activeSessions.size();
    }
}
