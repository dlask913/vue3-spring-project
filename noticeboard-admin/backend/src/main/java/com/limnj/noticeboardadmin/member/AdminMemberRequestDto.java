package com.limnj.noticeboardadmin.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminMemberRequestDto {
    Long id;
    String username;
    String password;

    @Builder
    public AdminMemberRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
