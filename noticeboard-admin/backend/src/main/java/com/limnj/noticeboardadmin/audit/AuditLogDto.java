package com.limnj.noticeboardadmin.audit;

import lombok.Getter;

@Getter
public class AuditLogDto {
    Long auditLogId;
    String eventType;
    Long userId;
    String username;
    String ipAddress;
    boolean isSuccess;
    String message;
}
