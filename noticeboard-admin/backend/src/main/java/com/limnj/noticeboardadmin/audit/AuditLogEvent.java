package com.limnj.noticeboardadmin.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class AuditLogEvent {
    Long auditLogId;
    String eventType;
    String actionType;
    Long userId;
    String username;
    String ipAddress;
    boolean isSuccess;
    String message;

    @Builder
    public AuditLogEvent(String eventType, String actionType, Long userId, String username, String ipAddress, boolean isSuccess, String message) {
        this.eventType = eventType;
        this.actionType = actionType;
        this.userId = userId;
        this.username = username;
        this.ipAddress = ipAddress;
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
