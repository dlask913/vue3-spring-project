package com.example.noticeboardservice.mapper.comment;

import com.example.noticeboardservice.dto.comment.ReplyRequestDto;
import com.example.noticeboardservice.dto.comment.ReplyResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReplyMapper {
    int insertReply(ReplyRequestDto replyDto);
    ReplyResponseDto findReply(Long replyId);
    List<ReplyResponseDto> findRepliesByCommentId(Long commentId);
    int updateReply(ReplyRequestDto replyDto);
    int deleteReply(Long replyId);
    List<ReplyResponseDto> findAllReplies();
    void deleteAll();
    Long calculateRepliesByCommentId(Long commentId);
}
