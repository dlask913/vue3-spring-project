package com.example.noticeboardservice.service.comment;

import com.example.noticeboardservice.dto.comment.ReplyRequestDto;
import com.example.noticeboardservice.dto.comment.ReplyResponseDto;
import com.example.noticeboardservice.mapper.comment.ReplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyMapper replyMapper;

    @Override
    public int insertReply(ReplyRequestDto replyDto) {
        return replyMapper.insertReply(replyDto);
    }

    @Override
    public List<ReplyResponseDto> findRepliesByCommentId(Long commentId) {
        return replyMapper.findRepliesByCommentId(commentId);
    }

    @Override
    public int updateReply(ReplyRequestDto replyDto) {
        return replyMapper.updateReply(replyDto);
    }

    @Override
    public int deleteReply(Long replyId) {
        return replyMapper.deleteReply(replyId);
    }

    @Override
    public Long calculateRepliesByCommentId(Long commentId) {
        return replyMapper.calculateRepliesByCommentId(commentId);
    }
}
