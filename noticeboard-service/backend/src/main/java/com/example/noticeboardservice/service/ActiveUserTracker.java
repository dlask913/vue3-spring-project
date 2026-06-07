package com.example.noticeboardservice.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        int currentUsers = activeUserCount.incrementAndGet();
        broadcastUserCount(currentUsers);
    }

    // 유저가 웹소켓 연결을 끊었을 때
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        int currentUserCount = activeUserCount.decrementAndGet();
        if (currentUserCount < 0) { // 음수가 되는 경우 0 으로 재설정
            activeUserCount.set(0);
            currentUserCount = 0;
        }
        broadcastUserCount(currentUserCount);
    }

    private void broadcastUserCount(int count) {
        // 구독 중인 클라이언트에게 현재 접속자 수 응답
        messagingTemplate.convertAndSend("/topic/active-users", count);
    }
}
