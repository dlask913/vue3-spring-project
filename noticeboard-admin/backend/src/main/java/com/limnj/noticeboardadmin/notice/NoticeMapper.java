package com.limnj.noticeboardadmin.notice;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {
    int insertNotice(NoticeRequestDto noticeRequestDto);
    int updateNotice(NoticeRequestDto noticeRequestDto);
    int deleteNotice(Long noticeId);
    NoticeResponseDto findNoticeById(Long noticeId);
    List<NoticeResponseDto> findAllNotices();
}
