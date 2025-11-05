package com.example.noticeboardservice.exception.member;

import com.example.noticeboardservice.exception.common.BizException;

public class PasswordMismatchException extends BizException {
    @Override
    public int getStatus() {
        return 400;
    }
    @Override
    public String getMessage() {
        return "비밀번호를 확인해주세요.";
    }
}
