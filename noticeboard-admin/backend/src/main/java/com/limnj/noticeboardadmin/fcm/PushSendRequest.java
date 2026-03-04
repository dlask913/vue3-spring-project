package com.limnj.noticeboardadmin.fcm;

import lombok.Getter;

@Getter
public class PushSendRequest {
    Long userId;
    String title;
    String body;
}
