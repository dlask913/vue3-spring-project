package com.limnj.noticeboardadmin.exception;

public class RefreshTokenInvalidException extends BizException{
    public RefreshTokenInvalidException() {
        super("Refresh Token 이 유효하지 않습니다.", 401);
    }
    public RefreshTokenInvalidException(String message) {
        super(message, 401);
    }

}
