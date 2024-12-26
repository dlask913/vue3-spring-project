package com.example.noticeboardservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddressRequestDto {
    Long memberId;
    String addressName;
    String roadAddressName;
    String longitude;
    String latitude;

    public void saveMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
