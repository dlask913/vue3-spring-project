package com.example.noticeboardservice.exception;

import lombok.Getter;

@Getter
abstract class BizException extends RuntimeException{
    private int status;
    private String message;
}
