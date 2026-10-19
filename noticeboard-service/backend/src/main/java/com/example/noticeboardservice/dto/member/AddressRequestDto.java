package com.example.noticeboardservice.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class AddressRequestDto {
    Long id;
    Long memberId;
    String addressName;
    String roadAddressName;
    BigDecimal longitude;
    BigDecimal latitude;

    public void saveMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Builder
    public AddressRequestDto(Long id, String addressName, String roadAddressName, BigDecimal longitude, BigDecimal latitude) {
        this.id = id;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
