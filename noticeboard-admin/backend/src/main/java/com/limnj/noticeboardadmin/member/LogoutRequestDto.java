package com.limnj.noticeboardadmin.member;

import lombok.Getter;

@Getter
public class LogoutRequestDto {
    String username;
    String fcmToken;
}
