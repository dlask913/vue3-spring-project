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
    PostType postType;

    public static enum PostType {
        NOTICE
    }

    @Builder
    public NoticeRequestDto(Long id, String title, String content, Long memberId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
    }

    void savePostType(){
        this.postType = PostType.NOTICE;
    }
}
