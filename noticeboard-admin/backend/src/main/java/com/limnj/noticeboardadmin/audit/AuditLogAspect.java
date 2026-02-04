package com.limnj.noticeboardadmin.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j @RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogEventListener auditLogEventListener;

    @Around("@annotation(auditLog)") // 메서드 전후 실행
    public Object logExecution(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        String eventType = auditLog.eventType();
        String actionType = auditLog.actionType();
        Object[] args = joinPoint.getArgs(); // 메서드 파라미터들

        long start = System.currentTimeMillis();
        try {
            // 원래 메서드 실행
            Object result = joinPoint.proceed();

            // 이벤트 발행
            AuditLogEvent event = AuditLogEvent.builder()
                    .eventType(eventType)
                    .actionType(actionType)
                    .userId(0L)
                    .username("username")
                    .ipAddress("0.0.0.0")
                    .isSuccess(true)
                    .build();

            auditLogEventListener.handleAuditLogEvent(event);

            log.info("SUCCESS - Action: [{}], eventType: [{}], Args: [{}]", actionType, eventType, args);
            return result;
        } catch (Exception e) {
            log.error("FAIL - Action: [{}], eventType: [{}], Reason: [{}]", actionType, eventType, e.getMessage());
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            log.info("Execution Time: {}ms", executionTime);
        }
    }
}