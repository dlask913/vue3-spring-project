package com.limnj.noticeboardadmin.fcm;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/** successCount는 기기 수신 완료가 아닌 FCM의 발송 요청 수락 건수 */
@Getter @NoArgsConstructor
public class SendPushResponseDto {
    int successCount;
    int failureCount;
    Map<String, Integer> errorCounts;

    @Builder
    public SendPushResponseDto(int successCount, int failureCount, Map<String, Integer> errorCounts) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.errorCounts = Map.copyOf(errorCounts);
    }
}
