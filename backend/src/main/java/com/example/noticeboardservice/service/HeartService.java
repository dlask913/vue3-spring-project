package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.HeartDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HeartService {
    HeartDto findHeart(Long memberId, Long commentId);
    int saveHeart(HeartDto heartDto);
    int deleteHeart(Long heartId);
}
