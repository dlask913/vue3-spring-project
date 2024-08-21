package com.example.noticeboardservice.exception;

public class TokenExpiredException extends BizException{
    @Override
    public int getStatus() {
        return 401;
    }

    @Override
    public String getMessage() {
        return "토큰이 만료되었습니다.";
    }
}
