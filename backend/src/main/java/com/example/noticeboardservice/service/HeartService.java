package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.HeartDto;
import org.springframework.stereotype.Service;

@Service
public interface HeartService {
    HeartDto findHeart(HeartDto hearDto);
    int saveHeart(HeartDto heartDto);
    int deleteHeart(Long heartId);
}
