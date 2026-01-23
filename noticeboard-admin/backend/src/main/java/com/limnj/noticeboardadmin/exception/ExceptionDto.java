package com.limnj.noticeboardadmin.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class ExceptionDto {
    int status;
    String errorCode;
    String message;

    @Builder
    public ExceptionDto(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
