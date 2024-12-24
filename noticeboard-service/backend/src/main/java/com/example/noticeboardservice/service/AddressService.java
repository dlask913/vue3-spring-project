package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.AddressRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {
    int insertAddress (AddressRequestDto addressDto);
}
