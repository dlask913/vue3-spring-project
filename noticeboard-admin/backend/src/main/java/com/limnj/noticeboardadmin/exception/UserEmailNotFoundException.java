package com.limnj.noticeboardadmin.exception;

public class UserEmailNotFoundException extends BizException{
    public UserEmailNotFoundException() {
        super("존재하지 않는 회원 이메일입니다.", 400);
    }
}
