package com.limnj.noticeboardadmin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    TOKEN_EXPIRED(401, "ACCESS TOKEN 만료"),
    TOKEN_MALFORMED(401, "잘못된 ACCESS TOKEN"),
    TOKEN_INVALID_SIGNATURE(401, "잘못된 ACCESS TOKEN"),
    ACCESS_DENIED(403,"API 접근 권한 부족"),
    REFRESH_TOKEN_EXPIRED(401,"REFRESH TOKEN 만료"),
    MEMBER_DUPLICATE(400,"중복된 회원"),
    PASSWORD_MISMATCH(401, "잘못된 비밀번호"),
    QR_NOT_GENERATE(400, "QR 미생성"),
    REFRESH_TOKEN_INVALID(401, "잘못된 REFRESH TOKEN"),
    EMAIL_NOT_FOUND(400, "존재하지 않는 회원"),
    MEMBER_NOT_FOUND(400, "존재하지 않는 회원"),
    ACCOUNT_LOCKED(423, "일시적 계정 잠금")
    ;

    private final int status;
    private final String description;
}
