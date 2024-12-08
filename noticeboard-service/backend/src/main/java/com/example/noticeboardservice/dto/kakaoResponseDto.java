package com.example.noticeboardservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;

@Getter
public class kakaoResponseDto{
    private List<AddressResponseDto> documents;

    @Getter
    public static class AddressResponseDto {
        @JsonProperty("address_name")
        String addressName; // 지번 주소
        @JsonProperty("road_address_name")
        String roadAddressName; // 도로명 주소
        @JsonProperty("category_name")
        String categoryName; // 카테고리 이름
        @JsonProperty("place_name")
        String placeName; // 장소 이름
        @JsonProperty("place_url")
        String placeUrl; // 장소 상세 페이지 URL
        String phone; // 전화번호
        double x; // 경도 (Longitude)
        double y;// 위도 (Latitude)
    }
}
