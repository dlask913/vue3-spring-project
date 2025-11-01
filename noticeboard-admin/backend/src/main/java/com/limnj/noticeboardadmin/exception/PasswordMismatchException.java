package com.limnj.noticeboardadmin.exception;

public class PasswordMismatchException extends BizException{
    public PasswordMismatchException() {
        super("비밀번호가 맞지 않습니다", 401);
    }
}
