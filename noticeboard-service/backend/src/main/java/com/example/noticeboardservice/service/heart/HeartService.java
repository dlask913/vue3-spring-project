package com.example.noticeboardservice.service.heart;

import com.example.noticeboardservice.dto.heart.HeartDto;
import org.springframework.stereotype.Service;

@Service
public interface HeartService {
    HeartDto findHeart(Long memberId, Long commentId);
    int saveHeart(HeartDto heartDto);
    int deleteHeart(Long heartId);
}
