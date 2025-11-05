package com.example.noticeboardservice.dto.member;


public record MemberResponseDto (
        Long id,
        String username,
        String email,
        String password,
        String imgUrl,
        String address
){
}
