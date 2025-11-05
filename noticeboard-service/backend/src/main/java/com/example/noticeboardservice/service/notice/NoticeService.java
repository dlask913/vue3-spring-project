package com.example.noticeboardservice.service.notice;

import com.example.noticeboardservice.dto.notice.NoticeRequestDto;
import com.example.noticeboardservice.dto.notice.NoticeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface NoticeService {
    int saveNotice(NoticeRequestDto noticeRequestDto);
    int updateNotice(Long noticeId, NoticeRequestDto noticeRequestDto);
    int deleteNotice(Long noticeId);
    NoticeResponseDto findNotice(Long noticeId, String email);
    List<NoticeResponseDto> findAllNotices(Map<String, String> params);
    List<NoticeResponseDto> findNoticeByMemberId(Long memberId);
    List<NoticeResponseDto> searchNoticeByPage(int page, int limit, String sortKey, String order, Map<String, String> params);
    List<NoticeResponseDto> findNoticesByUser(String email);
}
