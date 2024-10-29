package com.example.noticeboardservice.exception;

public class TokenInvalidException extends BizException{
    @Override
    public int getStatus() {
        return 401;
    }

    @Override
    public String getMessage() {
        return "토큰이 유효하지 않습니다.";
    }
}
