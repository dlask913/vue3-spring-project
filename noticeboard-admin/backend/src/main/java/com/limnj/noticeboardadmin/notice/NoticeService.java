package com.limnj.noticeboardadmin.notice;

import org.springframework.stereotype.Service;

@Service
public interface NoticeService {
    int saveNotice(NoticeRequestDto noticeRequestDto);
    int updateNotice(NoticeRequestDto noticeRequestDto);
    int deleteNotice(Long noticeId);
    NoticeResponseDto findNoticeByNoticeId(Long noticeId);
}
