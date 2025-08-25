package com.limnj.noticeboardadmin.exception;

import lombok.Getter;

@Getter
public abstract class BizException extends RuntimeException{
    private final int status;

    public BizException(String message, int status) {
        super(message);
        this.status = status;
    }
}

