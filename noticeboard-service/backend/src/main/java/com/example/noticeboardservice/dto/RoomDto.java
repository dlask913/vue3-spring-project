package com.example.noticeboardservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomDto {
    private Long id;
    @NotNull(message = "senderId 는 필수 값입니다")
    private Long senderId;
    @NotNull(message = "receiverId 는 필수 값입니다")
    private Long receiverId;

    @Builder
    public RoomDto(Long id, Long senderId, Long receiverId) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
