package com.example.noticeboardservice.exception;

public class MemberDuplicateException extends BizException{
    @Override
    public int getStatus() {
        return 400;
    }
    @Override
    public String getMessage() {
        return "이미 사용중인 이메일입니다.";
    }
}
