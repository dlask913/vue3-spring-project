package com.limnj.noticeboardadmin.notice;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeRequestDto {
    Long id;
    String title;
    String content;
    Long memberId;

    @Builder
    public NoticeRequestDto(Long id, String title, String content, Long memberId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
    }
}
