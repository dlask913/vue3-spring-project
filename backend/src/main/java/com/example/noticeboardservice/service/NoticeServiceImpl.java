package com.example.noticeboardservice.service;


import com.example.noticeboardservice.dto.NoticeRequestDto;
import com.example.noticeboardservice.dto.NoticeResponseDto;
import com.example.noticeboardservice.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service @Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final NoticeMapper noticeMapper;
    @Override
    public int saveNotice(NoticeRequestDto noticeRequestDto) {
        return noticeMapper.insertNotice(noticeRequestDto);
    }

    @Override
    public int updateNotice(Long noticeId,
                            NoticeRequestDto noticeRequestDto) {
        noticeRequestDto.saveNoticeId(noticeId);
        return noticeMapper.updateNotice(noticeRequestDto);
    }

    @Override
    public int deleteNotice(Long noticeId) {
        return noticeMapper.deleteNotice(noticeId);
    }

    @Override
    public NoticeResponseDto findNotice(Long noticeId, String email) {
        NoticeResponseDto findNotice = noticeMapper.findNotice(noticeId);
        if(!findNotice.getEmail().equals(email)){ // 내가 쓴 글이 아닐 때만 조회수 증가 ( todo: 게시글 없을 때 예외 처리 )
            int result = noticeMapper.incrementViewCount(noticeId);
        }
        return noticeMapper.findNotice(noticeId);
    }

    @Override
    public List<NoticeResponseDto> findAllNotices(Map<String, String> params) {
        return noticeMapper.findAllNotices(params);
    }

    @Override
    public List<NoticeResponseDto> searchNoticeByPage(int page, int limit, Map<String, String> params) {
        return noticeMapper.searchNoticesByPage((page-1)*limit, limit, params); // offset index 0부터 시작하므로, -1
    }

    @Override
    public List<NoticeResponseDto> findNoticeByMemberId(Long memberId) {
        return noticeMapper.findNoticeByMemberId(memberId);
    }

    @Override
    public List<NoticeResponseDto> findNoticesByUser(String email) {
        return noticeMapper.findNoticeByEmail(email);
    }
}
