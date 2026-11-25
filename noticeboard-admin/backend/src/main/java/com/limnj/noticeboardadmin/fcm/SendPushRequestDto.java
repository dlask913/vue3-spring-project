package com.limnj.noticeboardadmin.fcm;

import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;

import java.nio.charset.StandardCharsets;

@Getter
public class SendPushRequestDto {
    public interface UserPush extends Default {}

    @NotNull(groups = UserPush.class, message = "개별 발송 대상 userId가 필요합니다.")
    @Positive(message = "userId는 양수여야 합니다.")
    Long userId;
    @NotBlank(message = "알림 제목이 필요합니다.")
    String title;
    @NotBlank(message = "알림 본문이 필요합니다.")
    String body;

    @JsonIgnore
    @AssertTrue(message = "알림 제목과 본문은 합계 4000바이트 이하여야 합니다.")
    public boolean isContentSizeValid() {
        // FCM에서 알림 제목과 본문을 합쳐서 4000바이트로 제한
        if (title == null || body == null) return true;
        return (long) title.getBytes(StandardCharsets.UTF_8).length
                + body.getBytes(StandardCharsets.UTF_8).length <= 4000;
    }
}
