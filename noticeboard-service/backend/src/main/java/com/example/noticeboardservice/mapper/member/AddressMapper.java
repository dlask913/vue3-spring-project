package com.example.noticeboardservice.mapper.member;

import com.example.noticeboardservice.dto.member.AddressRequestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper {
    int insertAddress (AddressRequestDto addressDto);
}
