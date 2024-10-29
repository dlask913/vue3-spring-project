package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.CommentRequestDto;
import com.example.noticeboardservice.dto.CommentResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    int insertComment(CommentRequestDto commentDto);
    int updateComment(CommentRequestDto commentDto);
    int deleteComment(Long commentId);
    List<CommentResponseDto> findCommentsByNoticeId(Long noticeId);
    List<CommentResponseDto> findCommentsByUser(String email);

}
