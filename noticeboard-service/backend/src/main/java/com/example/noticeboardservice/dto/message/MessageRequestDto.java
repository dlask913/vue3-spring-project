package com.example.noticeboardservice.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageRequestDto {
    Long id;
    @NotNull(message = "senderId 은 필수 값 입니다.")
    Long senderId;
    @NotNull(message = "receiverId 은 필수 값 입니다.")
    Long receiverId;
    @NotNull(message = "content 은 필수 값 입니다.")
    String content;
    Long roomId;

    @Builder
    public MessageRequestDto(Long id, Long senderId, Long receiverId, String content) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public void saveRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
