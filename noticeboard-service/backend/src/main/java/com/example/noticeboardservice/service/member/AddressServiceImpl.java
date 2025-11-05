package com.example.noticeboardservice.service.member;

import com.example.noticeboardservice.dto.member.AddressRequestDto;
import com.example.noticeboardservice.mapper.member.AddressMapper;
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
