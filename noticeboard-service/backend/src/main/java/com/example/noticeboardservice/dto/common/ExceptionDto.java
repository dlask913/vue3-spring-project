package com.example.noticeboardservice.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class ExceptionDto {
    int status;
    String message;

    @Builder
    public ExceptionDto(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
