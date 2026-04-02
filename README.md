# vue3-spring-project
Web Application using Vue3 and Spring Boot

<hr>

### 🛠 Tech Stack

#### Client
(Vue / Pinia / Vue Router / Vite / Bootstrap / Quasar)

#### Server
(Java / Spring Boot / MyBatis / Spring Security / JWT / Swagger)

#### Infra
(Jenkins / Docker / Nginx / MySQL)

<hr>

### ✨ Features
- 상품 Category 생성 ([#7](https://github.com/dlask913/vue3-spring-project/issues/7)) opened on Oct 23, 2024 
- 단방향 메시지 주고받기 기능 구현 ([#22](https://github.com/dlask913/vue3-spring-project/issues/22)) opened on Feb 12, 2025
- 단방향 메시지 주고받기 → 채팅방 전환 ([#23](https://github.com/dlask913/vue3-spring-project/issues/23)) opened on Feb 26, 2025
- bulk 입력을 위한 엑셀 업로드 기능 구현 ([#69](https://github.com/dlask913/vue3-spring-project/issues/69)) opened on Dec 17, 2025
- 사용자 편의를 위한 PDF 다운로드 기능 구현 ([#70](https://github.com/dlask913/vue3-spring-project/issues/70)) opened on Dec 20, 2025
- FCM 을 활용한 브라우저 푸시 알림 - 전체 공지 ([#80](https://github.com/dlask913/vue3-spring-project/issues/80)) opened on Feb 21, 2026

<br>

### 🐞 Issues & Troubleshooting
- category 타입 오류로 인한 DefaultHandlerException 발생 ([#11](https://github.com/dlask913/vue3-spring-project/issues/11)) opened on Nov 12, 2024
- 서버와 클라이언트 간 타입 오류로 인한 AxiosError 발생 ([#15](https://github.com/dlask913/vue3-spring-project/issues/15)) opened on Dec 8, 2024
- Kakao Map API 요청 오류 수정 ([#16](https://github.com/dlask913/vue3-spring-project/issues/16)) opened on Dec 11, 2024
- Pagination UI 페이지 숫자 제한 ([#17](https://github.com/dlask913/vue3-spring-project/issues/17)) opened on Dec 13, 2024 
- Vue3 props 명시적 선언 ([#20](https://github.com/dlask913/vue3-spring-project/issues/20)) opened on Jan 24, 2025
- 게시글 조회 시 선택적 인증 처리 ([#21](https://github.com/dlask913/vue3-spring-project/issues/21)) opened on Feb 10, 2025
- 도커 컨테이너 데이터 유지를 위한 볼륨 마운트 ([#27](https://github.com/dlask913/vue3-spring-project/issues/27)) opened on Mar 26, 2025
- MyBatis 필드 미존재로 인한 자동 생성 키 매핑 실패 ([#29](https://github.com/dlask913/vue3-spring-project/issues/29)) opened on Apr 1, 2025
- 컨테이너 이중화로인한 InvaildSignaureException 발생 - secret key ([#32](https://github.com/dlask913/vue3-spring-project/issues/32)) opened on Apr 17, 2025
- 컨테이너 배포 상황에서 CORS 설정 ([#42](https://github.com/dlask913/vue3-spring-project/issues/42)) opened on Jun 6, 2025
- Docker Nginx 컨테이너 볼륨 마운트 오류 ([#46](https://github.com/dlask913/vue3-spring-project/issues/46)) opened on Jun 23, 2025
- spring 컨테이너 비정상 종료 - 리디렉션 문제 ([#47](https://github.com/dlask913/vue3-spring-project/issues/47)) opened on Jun 25, 2025
- Quasar CLI pinia 등록 실패 - 설정 문제 ([#55](https://github.com/dlask913/vue3-spring-project/issues/55)) opened on Jul 29, 2025
- 확장성을 고려하여 Image → File 로 테이블 변경 ([#57](https://github.com/dlask913/vue3-spring-project/issues/57)) opened on Sep 15, 2025
- Quasar CLI Axios 내 router 설정 에러 ([#60](https://github.com/dlask913/vue3-spring-project/issues/60)) opened on Oct 10, 2025
- 인터페이스 의존성 주입 실패 - `@Qualifier` ([#62](https://github.com/dlask913/vue3-spring-project/issues/62)) opened on Oct 16, 2025
- vueRouterMode 에 따른 경로 덧붙이는 문제 해결 - hash or history ([#73](https://github.com/dlask913/vue3-spring-project/issues/73)) opened on Jan 29, 2026
- @Transaction 으로 인한 데이터 오류 ([#79](https://github.com/dlask913/vue3-spring-project/issues/79)) opened on Feb 20, 2026
- Spring Security /logout redirect 문제 ([#84](https://github.com/dlask913/vue3-spring-project/issues/84)) opened on Mar 12, 2026
- 평문 데이터 복호화 예외 발생 ([#78](https://github.com/dlask913/vue3-spring-project/issues/78)) opened on Feb 19, 2026
- 정적 리소스 로딩 실패 문제 - MIME 타입 설정 ([#89](https://github.com/dlask913/vue3-spring-project/issues/89)) opened on Apr 1, 2026

<br>

### 🧠 Technical Decisions
- DTO 객체 타입 - record or class ([#4](https://github.com/dlask913/vue3-spring-project/issues/4)) opened on Sep 25, 2024
- MyBatis PK 반환 방식 - selectkey or useGeneratedKeys ([#5](https://github.com/dlask913/vue3-spring-project/issues/5)) opened on Oct 18, 2024
- EnumType 직렬화/역직렬화 - Jackson 애노테이션과 TypeHandler ([#6](https://github.com/dlask913/vue3-spring-project/issues/6)) opened on Oct 18, 2024
- 관리자 페이지 분리 ([#8](https://github.com/dlask913/vue3-spring-project/issues/8)) opened on Oct 29, 2024
- 프로젝트 간 공통으로 겹치는 부분 관리 방식 ([#9](https://github.com/dlask913/vue3-spring-project/issues/9)) opened on Oct 31, 2024
- 상품 입찰 결과 조회 쿼리 개선 - INDEX or 서브쿼리 or ROW_NUMBER() ([#12](https://github.com/dlask913/vue3-spring-project/issues/12)) opened on Nov 21, 2024
- 테스트 환경에서의 Security 필터 비활성화 ([#18](https://github.com/dlask913/vue3-spring-project/issues/18)) opened on Dec 17, 2024
- 클라이언트 예외 처리 방식 - `try~catch` or axios 인터셉터 ([#19](https://github.com/dlask913/vue3-spring-project/issues/19)) opened on Jan 13, 2025
- INDEX 성능 전후 비교 - 옵티마이저 실행 계획을 통해 개선된 정도 확인 ([#25](https://github.com/dlask913/vue3-spring-project/issues/25)) opened on Mar 10, 2025
- INDEX 성능 전후 비교 - Artillery 부하 테스트 ([#26](https://github.com/dlask913/vue3-spring-project/issues/26)) opened on Mar 11, 2025
- Ningx 를 통한 컨테이너 이중화 ([#30](https://github.com/dlask913/vue3-spring-project/issues/30)) opened on Apr 7, 2025 
- 외부 라이브러리 jar 생성하여 메일 전송 ([#33](https://github.com/dlask913/vue3-spring-project/issues/33)) opened on Apr 22, 2025
- 스프링 컨테이너 로그 백업을 위한 추가 컨테이너 구성 - 로그 백업 스크립트 ([#36](https://github.com/dlask913/vue3-spring-project/issues/36)) opened on May 12, 2025
- 입찰 시, 정렬 기준 정책 부재로 인한 동시성 문제, 분산 환경에서 Artillery 를 통한 문제 상황 재현 ([#37](https://github.com/dlask913/vue3-spring-project/issues/37), [#38](https://github.com/dlask913/vue3-spring-project/issues/38)) opened on May 24, 2025
- 소규모 프로젝트에서 인터페이스 유지 ([#44](https://github.com/dlask913/vue3-spring-project/issues/44)) opened on Jun 13, 2025
- 테스트 환경에서의 H2 임베디드 모드 ([#45](https://github.com/dlask913/vue3-spring-project/issues/45)) opened on Jun 17, 2025
- 로그 백업 컨테이너 → logback + Cron 으로 전환 ([#48](https://github.com/dlask913/vue3-spring-project/issues/48)) opened on Jun 27, 2025
- prod 환경 변수 관리 - Jenkins Credentials ([#49](https://github.com/dlask913/vue3-spring-project/issues/49)) opened on Jun 28, 2025
- Vite + CKEditor 적용 실패 ([#52](https://github.com/dlask913/vue3-spring-project/issues/52)) opened on Jul 12, 2025
- Admin Fronend Quasar CLI 전환 ([#53](https://github.com/dlask913/vue3-spring-project/issues/53)) opened on Jul 20, 2025
- 서비스 접근 로그 기록 ([#59](https://github.com/dlask913/vue3-spring-project/issues/59)) opened on Sep 27, 2025
- 구현체에 공통 기능 적용을 위한 default 메서드 활용 ([#63](https://github.com/dlask913/vue3-spring-project/issues/63)) opened on Oct 23, 2025
- 서버 예외 처리 방식 ([#72](https://github.com/dlask913/vue3-spring-project/issues/72)) opened on Jan 24, 2026
- 감사용 로그 기록 방식 - AOP + 이벤트리스너 활용 ([#74](https://github.com/dlask913/vue3-spring-project/issues/74)) opened on Jan 31, 2026
- 클라이언트 토큰 관리 방법 - LocalStorage 와 Cookie ([#56](https://github.com/dlask913/vue3-spring-project/issues/56)) opened on Jul 31, 2025

<br>

### 🔐 Security
- OpenSSL 적용 ([#31](https://github.com/dlask913/vue3-spring-project/issues/31)) opened on Apr 9, 2025
- XSS 필터 적용 - Lucy or XssRequestWrapper 구현 ([#51](https://github.com/dlask913/vue3-spring-project/issues/51)) opened on Jul 4, 2025
- JWT Refresh Token 발급 로직 추가 ([#54](https://github.com/dlask913/vue3-spring-project/issues/54)) opened on Jul 25, 2025
- MFA 적용 - 이메일 인증 ([#61](https://github.com/dlask913/vue3-spring-project/issues/61)) opened on Oct 12, 2025
- 계정 인증 보안 설정 - 비밀번호 암호화, 로그인 시도 횟수 제한, 동시 접속 ([#64](https://github.com/dlask913/vue3-spring-project/issues/64)) opened on Oct 29, 2025
- MFA 적용 - Google OTP 인증 ([#65](https://github.com/dlask913/vue3-spring-project/issues/65)) opened on Nov 1, 2025
- 저장된 민감 정보 관리를 위한 데이터 양방향 암호화 - AES Algorithm ([#75](https://github.com/dlask913/vue3-spring-project/issues/75)) opened on Feb 1, 2026
- 토큰 검증 플로우 정리 - access token 및 refresh token ([#24](https://github.com/dlask913/vue3-spring-project/issues/24)) opened on Mar 6, 2025
- 사용자 권한 별 접근 제어 - Spring CustomAccessDeniedHandler 와 Vue 라우터 가드 ([#71](https://github.com/dlask913/vue3-spring-project/issues/71)) opened on Jan 11, 2026
- Nginx 보안 설정 - HTTPS, TLS, 보안 헤더, 타임아웃 ([#88](https://github.com/dlask913/vue3-spring-project/issues/88)) opened on Mar 28, 2026

<br>

### 🚀 Deployment
- 서버, 클라이언트, DB 도커 컨테이너 배포 ([#28](https://github.com/dlask913/vue3-spring-project/issues/28)) opened on Mar 27, 2025
- 컨테이너 자동 배포 - Jenkinsfile ([#34](https://github.com/dlask913/vue3-spring-project/issues/34)) opened on Apr 23, 2025
- 컨테이너 배포 방식 전환 - to Docker Image 기반 배포 ([#43](https://github.com/dlask913/vue3-spring-project/issues/43)) opened on Jun 12, 2025

<br>

### 📊 Monitoring & Alerting
- 서버 상태 모니터링하여 Slack 알림 ([#40](https://github.com/dlask913/vue3-spring-project/issues/40)) opened on May 28, 2025
- 모니터링을 위한 actuator 와 prometheus 적용 with Slack ([#39](https://github.com/dlask913/vue3-spring-project/issues/39)) opened on May 27, 2025
- 그라파나를 통한 지표 시각화 ([#67](https://github.com/dlask913/vue3-spring-project/issues/67)) opened on Dec 11, 2025

<hr>
