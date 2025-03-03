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

    @Builder
    public RoomDto(Long id, Long lowerId, Long higherId) {
        this.id = id;
        this.lowerId = lowerId;
        this.higherId = higherId;
    }
}
