package com.example.noticeboardservice.mapper.notice;

import com.example.noticeboardservice.dto.notice.NoticeRequestDto;
import com.example.noticeboardservice.dto.notice.NoticeResponseDto;
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
            @Param("sortKey") String sortKey,
            @Param("order") String order,
            @Param("params") Map<String, String> params
    );
    List<NoticeResponseDto> findNoticeByMemberId(Long memberId);
    void deleteAll();
    List<NoticeResponseDto> findNoticeByEmail(String email);
}
