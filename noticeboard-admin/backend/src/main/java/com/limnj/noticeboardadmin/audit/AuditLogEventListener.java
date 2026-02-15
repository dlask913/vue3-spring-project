package com.limnj.noticeboardadmin.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component @Slf4j
@RequiredArgsConstructor
public class AuditLogEventListener {
    private final AuditLogMapper auditLogMapper;

    @Async // 비즈니스 로직과 별개의 쓰레드에서 동작
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAuditLogEvent(AuditLogEvent auditLogEvent) {
        try {
            auditLogMapper.insertAuditLog(auditLogEvent);
            log.info("Audit Log saved: {} - {}", auditLogEvent.getEventType(), auditLogEvent.getActionType());
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }
}
