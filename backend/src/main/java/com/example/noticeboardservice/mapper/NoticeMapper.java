package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.NoticeRequestDto;
import com.example.noticeboardservice.dto.NoticeResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoticeMapper {
    int insertNotice(NoticeRequestDto noticeRequestDto);
    int updateNotice(NoticeRequestDto noticeRequestDto);
    int deleteNotice(Long noticeId);
    NoticeResponseDto findNotice(Long noticeId);
    int incrementViewCount(Long noticeId);
    List<NoticeResponseDto> findAllNotices(@Param("params") Map<String, String> params);
    List<NoticeResponseDto> searchNoticesByPage(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("params") Map<String, String> params
    );
    List<NoticeResponseDto> findNoticeByMemberId(Long memberId);
    void deleteAll();
    List<NoticeResponseDto> findNoticeByEmail(String email);
}
