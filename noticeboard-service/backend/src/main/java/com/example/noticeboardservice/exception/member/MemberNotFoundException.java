package com.example.noticeboardservice.exception.member;


import com.example.noticeboardservice.exception.common.BizException;

public class MemberNotFoundException extends BizException {
    @Override
    public int getStatus() {
        return 400;
    }
    @Override
    public String getMessage() {
        return "회원 정보가 존재하지 않습니다.";
    }
}
