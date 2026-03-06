package com.limnj.noticeboardadmin.fcm;

import lombok.Getter;

@Getter
public class SendPushRequestDto {
    Long userId;
    String title;
    String body;
}
