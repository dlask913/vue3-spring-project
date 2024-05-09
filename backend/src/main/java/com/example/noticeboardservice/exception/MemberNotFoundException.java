package com.example.noticeboardservice.exception;

import lombok.Getter;

public class MemberNotFoundException extends BizException{
    @Override
    public int getStatus() {
        return 400;
    }
    @Override
    public String getMessage() {
        return "회원 정보가 존재하지 않습니다.";
    }
}
