package com.limnj.noticeboardadmin.member;

import lombok.Getter;

@Getter
public class AdminMemberRequestDto {
    String username;
    String password;
    String userType;
}
