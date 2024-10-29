package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.CommentRequestDto;
import com.example.noticeboardservice.dto.CommentResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    int insertComment(CommentRequestDto commentDto);
    int updateComment(CommentRequestDto commentDto);
    int deleteComment(Long commentId);
    List<CommentResponseDto> findCommentsByNoticeId(Long noticeId);
    List<CommentResponseDto> findAllComments();
    void deleteAll();
    List<CommentResponseDto> findCommentsByEmail(String email);
}
