package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.NoticeRequestDto;
import com.example.noticeboardservice.dto.NoticeResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {
    int insertNotice(NoticeRequestDto noticeRequestDto);
    int updateNotice(NoticeRequestDto noticeRequestDto);
    int deleteNotice(Long noticeId);
    NoticeResponseDto findNotice(Long noticeId);
    List<NoticeResponseDto> findAllNotices();
    List<NoticeResponseDto> findNoticeByMemberId(Long memberId);
    void deleteAll();

}
