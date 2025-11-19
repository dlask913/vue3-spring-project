package com.limnj.noticeboardadmin.notice;

import lombok.Getter;

@Getter
public class NoticeResponseDto {
    Long id;
    String title;
    String content;
    String postDate;
}
