package com.example.noticeboardservice.exception.token;

import com.example.noticeboardservice.exception.common.BizException;

public class TokenInvalidException extends BizException {
    @Override
    public int getStatus() {
        return 401;
    }

    @Override
    public String getMessage() {
        return "토큰이 유효하지 않습니다.";
    }
}
