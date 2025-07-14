package com.limnj.noticeboardadmin.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
    public NoticeResponseDto findNoticeByNoticeId(Long noticeId) {
        return noticeMapper.findNoticeById(noticeId);
    }
}
