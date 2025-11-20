package com.limnj.noticeboardadmin.exception;

public class QrNotGenerateException extends BizException {
    public QrNotGenerateException() {
        super("QR 생성을 하지 않은 회원입니다.", 400);
    }
}
