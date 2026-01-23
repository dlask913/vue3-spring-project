package com.limnj.noticeboardadmin.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException{
    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}

