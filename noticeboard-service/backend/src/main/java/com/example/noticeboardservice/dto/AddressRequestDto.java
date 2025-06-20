package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddressRequestDto {
    Long id;
    Long memberId;
    String addressName;
    String roadAddressName;
    String longitude;
    String latitude;

    public void saveMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Builder
    public AddressRequestDto(Long id, String addressName, String roadAddressName, String longitude, String latitude) {
        this.id = id;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
