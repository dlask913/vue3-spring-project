package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.AddressRequestDto;
import com.example.noticeboardservice.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
    private final AddressMapper addressMapper;
    @Override
    public int insertAddress(AddressRequestDto requestDto) {
        return addressMapper.insertAddress(requestDto);
    }
}
