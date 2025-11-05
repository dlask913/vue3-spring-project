package com.example.noticeboardservice.dto.notice;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {
    Long id;
    String title;
    String content;
    Long memberId;
    PostType postType;

    @Builder
    public NoticeRequestDto(Long id, String title, String content, Long memberId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.postType = PostType.POST;
    }
    public void saveNoticeId(Long noticeId){ // notice 수정 시 pathvariable 저장 용도
        this.id = noticeId;
    }
}
