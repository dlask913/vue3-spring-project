package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.NoticeRequestDto;
import com.example.noticeboardservice.dto.NoticeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NoticeService {
    int saveNotice(NoticeRequestDto noticeRequestDto);
    int updateNotice(NoticeRequestDto noticeRequestDto);
    int deleteNotice(Long noticeId);
    NoticeResponseDto findNotice(Long noticeId);
    List<NoticeResponseDto> findAllNotices();
    List<NoticeResponseDto> findNoticeByMemberId(Long memberId);
}
