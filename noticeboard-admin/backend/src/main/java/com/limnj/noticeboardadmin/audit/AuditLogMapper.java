package com.limnj.noticeboardadmin.audit;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {
    void insertAuditLog(AuditLogDto auditLogDto);
}
