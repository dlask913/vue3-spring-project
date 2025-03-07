package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomDto {
    private Long id;
    private Long lowerId;
    private Long higherId;
    private String lowerIdUsername;
    private String higherIdUsername;

    @Builder
    public RoomDto(Long id, Long lowerId, Long higherId, String lowerIdUsername, String higherIdUsername) {
        this.id = id;
        this.lowerId = lowerId;
        this.higherId = higherId;
        this.lowerIdUsername = lowerIdUsername;
        this.higherIdUsername = higherIdUsername;
    }
}
