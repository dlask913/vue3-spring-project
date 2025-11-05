package com.example.noticeboardservice.exception.common;

import lombok.Getter;

@Getter
public abstract class BizException extends RuntimeException{
    public int status;
    public String message;
}
