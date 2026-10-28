
## 데이터베이스 준비
- DBMS: PostgreSQL 17 (관리자와 일반 서비스 공용)
- 초기 구축을 위한 DDL 스크립트 실행
- UTF-8 파일을 psql이 UHC(한국어 Windows 인코딩)로 해석해서 오류 발생 -> encoding 설정 추가 ([공식문서](https://www.postgresql.org/docs/17/multibyte.html))

```powershell
ps>CREATE DATABASE noticeboard OWNER noticeboard_app;
\q
ps>$env:PGCLIENTENCODING = "UTF8"
ps>& "C:\Program Files\PostgreSQL\17\bin\psql.exe" `
  -h localhost -p 5432 -U noticeboard_app -d noticeboard -W `
  -v ON_ERROR_STOP=1 `
  -f "C:\vue3-project\vue3-spring-project\database\init-postgresql.sql"
```
- 도커 실행
```bash
docker compose --env-file noticeboard-service/.env.local -f noticeboard-service/docker-compose.yml up -d postgres
```


## 연결 및 실행
- 메일, JWT, DB 계정은 `noticeboard-admin/.env.local`에서 관리
- 운영 환경에서는 실제 계정과 비밀번호를 환경 변수로 지정

| 환경 변수 | 기본값 |
| --- | --- |
| `POSTGRES_HOST` | `localhost` |
| `POSTGRES_PORT` | `5432` |
| `POSTGRES_DB` | `noticeboard` |
| `POSTGRES_TEST_DB` | `testboard` (`test` 프로필 전용) |
| `POSTGRES_USER` | `.env.local`에서 지정 |
| `POSTGRES_PASSWORD` | `.env.local`에서 지정 |

- 실행하기
```powershell
cd noticeboard-admin/backend
.\gradlew.bat bootRun
```
