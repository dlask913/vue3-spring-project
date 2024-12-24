package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.AddressRequestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper {
    int insertAddress (AddressRequestDto addressDto);
}
