package com.example.noticeboardservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MemberDto {
    Long id;
    @NotNull(message = "email 은 필수 값 입니다.")
    String email;
    @NotNull(message = "username 은 필수 값 입니다.")
    String username;
    @NotNull(message = "password 은 필수 값 입니다.")
    String password;
}
