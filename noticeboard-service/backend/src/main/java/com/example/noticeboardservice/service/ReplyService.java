package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ReplyRequestDto;
import com.example.noticeboardservice.dto.ReplyResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReplyService {
    int insertReply(ReplyRequestDto replyDto);
    List<ReplyResponseDto> findRepliesByCommentId(Long commentId);
    int updateReply(ReplyRequestDto replyDto);
    int deleteReply(Long replyId);
    Long calculateRepliesByCommentId(Long commentId);
}
