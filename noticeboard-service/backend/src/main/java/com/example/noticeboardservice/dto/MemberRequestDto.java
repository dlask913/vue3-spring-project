package com.example.noticeboardservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRequestDto {
    Long id;
    @NotNull(message = "email 은 필수 값 입니다.")
    String email;
    @NotNull(message = "username 은 필수 값 입니다.")
    String username;
    @NotNull(message = "password 은 필수 값 입니다.")
    String password;
    AddressRequestDto address;

    @Builder
    public MemberRequestDto(Long id, String email, String username, String password, AddressRequestDto address) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.address = address;
    }
}
