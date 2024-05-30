package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.HeartDto;
import com.example.noticeboardservice.mapper.HeartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService{
    private final HeartMapper heartMapper;

    @Override
    public HeartDto findHeart(HeartDto hearDto) {
        return heartMapper.findHeart(hearDto);
    }

    @Override
    public int saveHeart(HeartDto heartDto) {
        return heartMapper.saveHeart(heartDto);
    }

    @Override
    public int deleteHeart(Long heartId) {
        return heartMapper.deleteHeart(heartId);
    }
}
