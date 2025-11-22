package com.limnj.noticeboardadmin.notice;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface NoticeService {
    int saveNotice(NoticeRequestDto noticeRequestDto, MultipartFile noticeFile);
    int updateNotice(NoticeRequestDto noticeRequestDto, MultipartFile noticeFile);
    int deleteNotice(Long noticeId);
    NoticeResponseDto findNoticeByNoticeId(Long noticeId);
    List<NoticeResponseDto> findAllNotices();
}
