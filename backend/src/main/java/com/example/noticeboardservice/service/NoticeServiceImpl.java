package com.example.noticeboardservice.service;


import com.example.noticeboardservice.dto.NoticeRequestDto;
import com.example.noticeboardservice.dto.NoticeResponseDto;
import com.example.noticeboardservice.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final NoticeMapper noticeMapper;
    @Override
    public int saveNotice(NoticeRequestDto noticeRequestDto) {
        return noticeMapper.insertNotice(noticeRequestDto);
    }

    @Override
    public int updateNotice(NoticeRequestDto noticeRequestDto) {
        return noticeMapper.updateNotice(noticeRequestDto);
    }

    @Override
    public int deleteNotice(Long noticeId) {
        return noticeMapper.deleteNotice(noticeId);
    }

    @Override
    public NoticeResponseDto findNotice(Long noticeId) {
        return noticeMapper.findNotice(noticeId);
    }

    @Override
    public List<NoticeResponseDto> findAllNotices() {
        return noticeMapper.findAllNotices();
    }

    @Override
    public List<NoticeResponseDto> findNoticeByMemberId(Long memberId) {
        return noticeMapper.findNoticeByMemberId(memberId);
    }
}
