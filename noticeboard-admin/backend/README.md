# Admin 백엔드 API

## 회원 및 인증

| 메서드 | 경로 | 기능 | 현재 필요한 인증 | 허용 역할 |
| --- | --- | --- | --- | --- |
| POST | `/member` | Admin 계정 가입 | 없음 | 제한 없음 |
| POST | `/login` | 1차 로그인 후 이메일 인증 코드 발송 | 아이디·비밀번호 | 제한 없음 |
| POST | `/email-verify` | 이메일 코드 확인 후 로그인 토큰 발급 | 이메일 인증 코드 | 제한 없음 |
| POST | `/resend-code` | 요청한 이메일로 인증 코드 재발송 | 없음 | 제한 없음 |
| POST | `/qr-register` | 로그인한 본인 계정의 OTP 키 등록 및 QR 반환 | JWT | 로그인한 사용자 |
| POST | `/qr-verify` | 등록된 OTP 확인 후 로그인 토큰 발급 | 인증 앱의 6자리 OTP | 제한 없음 |
| POST | `/refresh` | Access Token 재발급 요청 | `refreshToken` 쿠키 | 제한 없음 |
| POST | `/logout` | Spring Security 로그아웃 필터가 처리하고 200 응답 | 없음 | 제한 없음 |


## 공지 및 카테고리

| 메서드 | 경로 | 기능 | 현재 필요한 인증 | 허용 역할 |
| --- | --- | --- | --- | --- |
| GET | `/notices` | 공지 목록 조회 | JWT | USER, MANAGER, ADMIN |
| GET | `/notice/{noticeId}` | 공지 상세 조회 | JWT | USER, MANAGER, ADMIN |
| POST | `/notice` | 공지 작성 | JWT | USER, MANAGER, ADMIN |
| PUT | `/notice` | 공지 수정 | JWT | USER, MANAGER, ADMIN |
| DELETE | `/notice/{noticeId}` | 공지 삭제 | JWT | USER, MANAGER, ADMIN |
| GET | `/categories` | 카테고리 목록 조회 | JWT | USER, MANAGER, ADMIN |
| POST | `/category` | 이미지와 함께 카테고리 생성 | JWT | MANAGER, ADMIN |
| DELETE | `/category/{categoryId}` | 카테고리 삭제 | JWT | MANAGER, ADMIN |

## 재고

| 메서드 | 경로 | 기능 | 현재 필요한 인증 | 허용 역할 |
| --- | --- | --- | --- | --- |
| GET | `/inventories` | 재고 목록 조회 | JWT | MANAGER, ADMIN |
| GET | `/inventories/{inventory-name}` | 이름으로 재고 검색 | JWT | MANAGER, ADMIN |
| POST | `/inventories/parser` | 업로드한 엑셀 파일을 재고 목록으로 변환 | JWT | MANAGER, ADMIN |
| POST | `/inventories/bulk` | 재고 목록 일괄 저장 | JWT | MANAGER, ADMIN |
| GET | `/inventories/pdf` | 재고 목록 PDF 다운로드 | JWT | MANAGER, ADMIN |

## 푸시 알림, 통계 및 테스트

| 메서드 | 경로 | 기능 | 현재 필요한 인증 | 허용 역할 |
| --- | --- | --- | --- | --- |
| POST | `/fcm/token` | 익명 기기 등록 (요청의 userId는 무시) | 없음 | 제한 없음 |
| POST | `/push/all` | 등록된 모든 기기에 푸시 발송 | JWT | ADMIN |
| POST | `/push/user` | 지정한 사용자의 기기에 푸시 발송 | JWT | ADMIN |
| GET | `/daily/logs/{from-date}/{to-date}` | 기간별 일일 로그 통계 조회 | JWT | ADMIN |
| GET | `/test/cpu` | CPU 부하 테스트 | 호출 차단 | 없음 |
