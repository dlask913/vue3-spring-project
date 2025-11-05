package com.example.noticeboardservice.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @NoArgsConstructor
public class AccessLogDto {
    private Long id;
    private String method;
    private String uri;
    private String clientIp;
    private LocalDateTime createdAt;

    @Builder
    public AccessLogDto(Long id, String method, String uri, String clientIp, LocalDateTime createdAt) {
        this.id = id;
        this.method = method;
        this.uri = uri;
        this.clientIp = clientIp;
        this.createdAt = createdAt;
    }
}
