package com.example.noticeboardservice.dto;


public record MemberResponseDto (
        Long id,
        String email,
        String username,
        String password,
        String address,
        String imgUrl
){
}
