package com.example.noticeboardservice.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ActiveUserTracker {
    private final SimpMessagingTemplate messagingTemplate;
    private final AtomicInteger activeUserCount = new AtomicInteger(0);

    public ActiveUserTracker(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 유저가 웹소켓에 연결했을 때
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        int currentUsers = activeUserCount.incrementAndGet();
        System.out.println("🟢 유저 연결됨! 현재 접속자: " + currentUsers);
        broadcastUserCount(currentUsers);
    }

    // 유저가 웹소켓 연결을 끊었을 때
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        int currentUserCount = Math.max(0, activeUserCount.decrementAndGet());
        System.out.println("X 유저 연결 해제! 현재 접속자: " + currentUserCount);
        broadcastUserCount(currentUserCount);
    }

    // 유저가 구독(subscribe)을 마친 직후 그 유저 포함 전체에게 현재 카운트 전송
    @EventListener
    public void handleSessionSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if ("/topic/active-users".equals(headerAccessor.getDestination())) {
            broadcastUserCount(getActiveUserCount());
        }
    }

    private void broadcastUserCount(int count) {
        // 구독 중인 클라이언트에게 현재 접속자 수 응답
        messagingTemplate.convertAndSend("/topic/active-users", count);
    }

    // 현재 접속자 수 반환 메서드
    public int getActiveUserCount() {
        return activeUserCount.get();
    }
}
