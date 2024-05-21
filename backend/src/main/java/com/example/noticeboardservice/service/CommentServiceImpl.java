package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.CommentRequestDto;
import com.example.noticeboardservice.dto.CommentResponseDto;
import com.example.noticeboardservice.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    @Override
    public int insertComment(CommentRequestDto commentDto) {
        return commentMapper.insertComment(commentDto);
    }

    @Override
    public int updateComment(CommentRequestDto commentDto) {
        return commentMapper.updateComment(commentDto);
    }

    @Override
    public int deleteComment(Long commentId) {
        return commentMapper.deleteComment(commentId);
    }

    @Override
    public List<CommentResponseDto> findCommentsByNoticeId(Long noticeId) {
        return commentMapper.findCommentsByNoticeId(noticeId);
    }
}
