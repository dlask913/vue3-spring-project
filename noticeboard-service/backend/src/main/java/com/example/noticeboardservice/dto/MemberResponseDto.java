package com.example.noticeboardservice.dto;


public record MemberResponseDto (
        Long id,
        String username,
        String email,
        String password,
        String address,
        String imgUrl
){
}
