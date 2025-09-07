package com.limnj.noticeboardadmin.exception;

public class MemberDuplicateException extends BizException {
    public MemberDuplicateException() {
        super("이미 가입되어있는 회원입니다.", 400);
    }
}
