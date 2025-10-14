package com.limnj.noticeboardadmin.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminMemberRequestDto {
    Long id;
    String username;
    String email;
    String password;

    @Builder
    public AdminMemberRequestDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
