package com.limnj.noticeboardadmin.member;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties("username") // 로그아웃 대상 결정에는 사용 X
public class LogoutRequestDto {
    String fcmToken;
}
