package com.example.noticeboardservice.dto;

import lombok.Getter;

@Getter
public class MemberResponseDto {
    Long id;
    String email;
    String username;
    String password;
    String address;
    String imgUrl;
}
