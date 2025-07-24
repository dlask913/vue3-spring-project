package com.limnj.noticeboardadmin.notice;

import lombok.Getter;

@Getter
public class NoticeRequestDto {
    Long id;
    String title;
    String content;
    Long memberId;
}
