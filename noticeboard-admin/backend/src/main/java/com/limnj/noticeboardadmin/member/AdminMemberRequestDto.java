package com.limnj.noticeboardadmin.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminMemberRequestDto {
    Long id;
    String username;
    String email;
    String password;
    Role role;

    @Builder
    public AdminMemberRequestDto(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    void saveEncodedPassword(String encodedPassword){
        this.password = encodedPassword;
    }
}
