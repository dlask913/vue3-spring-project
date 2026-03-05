package com.limnj.noticeboardadmin.fcm;

import lombok.Getter;

@Getter
public class PushSendRequestDto {
    Long userId;
    String title;
    String body;
}
