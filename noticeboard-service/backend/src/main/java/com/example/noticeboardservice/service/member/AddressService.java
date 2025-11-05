package com.example.noticeboardservice.service.member;

import com.example.noticeboardservice.dto.member.AddressRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {
    int insertAddress (AddressRequestDto addressDto);
}
