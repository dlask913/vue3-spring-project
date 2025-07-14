package com.limnj.noticeboardadmin.notice;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper {
    int insertNotice(NoticeRequestDto noticeRequestDto);
    int updateNotice(NoticeRequestDto noticeRequestDto);
    int deleteNotice(Long noticeId);
    NoticeResponseDto findNoticeById(Long noticeId);
}
