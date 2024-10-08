package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.ReplyRequestDto;
import com.example.noticeboardservice.dto.ReplyResponseDto;
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
