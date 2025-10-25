package com.limnj.noticeboardadmin.member;

import lombok.Getter;

@Getter
public class AdminMemberResponseDto {
    private Long id;
    private String email;
    private String username;
    private String password;
}
