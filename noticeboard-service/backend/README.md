# backend

### 개발 환경

- Build Tool: Gradle
- Framework: Spring Boot 3.2.5
- Programming Language: Java / Kotlin, JDK 17
- SQL Mapper: MyBatis
- DBMS: PostgreSQL 15

### PostgreSQL 초기화

새 데이터베이스를 사용하는 구성이며 기존 MySQL 데이터는 이관하지 않습니다.
테이블, 인덱스, 외래 키, 수정 시각 트리거의 기준 스크립트는
[`../init-postgresql.sql`](../init-postgresql.sql)입니다.
[`../init.sql`](../init.sql)은 이전 MySQL 스크립트이므로 PostgreSQL에서 실행하지 않습니다.

저장소 루트에서 PostgreSQL만 실행합니다.

```powershell
docker compose -f noticeboard-service/docker-compose.yml up -d postgres
docker compose -f noticeboard-service/docker-compose.yml ps postgres
docker compose -f noticeboard-service/docker-compose.yml logs postgres
```

기본 접속 정보는 아래와 같습니다. `POSTGRES_DB`, `POSTGRES_USER`,
`POSTGRES_PASSWORD` 환경 변수로 DB와 애플리케이션의 값을 함께 변경할 수 있습니다.
애플리케이션의 접속 호스트와 포트는 `POSTGRES_HOST`, `POSTGRES_PORT`로 변경합니다.
`POSTGRES_PORT`는 Compose의 호스트 노출 포트에도 적용됩니다.
Compose 내부 백엔드는 항상 `postgres:5432`에 접속합니다.

| 항목 | 기본값 |
| --- | --- |
| DB 이름 | `noticeboard` |
| 사용자 | `myuser` |
| 비밀번호 | `mypass` (로컬 개발용) |
| IDE / 로컬 실행 주소 | `localhost:5432` |
| Compose 내부 주소 | `postgres:5432` |

Compose는 `init-postgresql.sql`을 초기화 디렉터리에 연결합니다.
공식 PostgreSQL 이미지의 초기화 스크립트는 **데이터 볼륨이 비어 있을 때만** 실행됩니다.
컨테이너 재시작으로 기존 스키마가 갱신되지는 않습니다.
기존 볼륨의 DB 이름이나 계정도 환경 변수 변경만으로 바뀌지 않습니다.
([PostgreSQL Docker 이미지 설명](https://hub.docker.com/_/postgres))

Docker 외부에 PostgreSQL을 직접 설치했다면, 빈 `noticeboard` 데이터베이스와
접속 계정을 먼저 만든 뒤 저장소 루트에서 실행합니다. 비밀번호는 프롬프트에 입력합니다.

```powershell
psql -h localhost -p 5432 -U myuser -d noticeboard -W -v ON_ERROR_STOP=1 -f noticeboard-service/init-postgresql.sql
```

이 스크립트는 신규 DB용입니다. 이미 테이블이 있는 DB에는 재실행하지 않습니다.
DB 생성과 테이블 초기화는 별개이며, `psql` 실행에는 대상 DB의 테이블 생성 권한이 필요합니다.

### 로컬 백엔드 실행

`local` 프로필은 `localhost`의 PostgreSQL에 접속하고,
`dev` 프로필은 Compose의 `postgres` 호스트에 접속합니다.
IDE에서도 JDK 17과 `local` 프로필을 사용합니다.

```powershell
Set-Location noticeboard-service/backend
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
```

Compose로 백엔드까지 실행할 때는 `dev` 프로필에서 요구하는 메일, JWT, Kakao,
Slack 환경 변수도 설정해야 합니다. DB 초기화 스크립트는 계정이나 게시글 등
애플리케이션 데이터를 생성하지 않습니다.

### 테스트

Docker Desktop 또는 Docker 데몬이 실행 중인 상태에서 백엔드 디렉터리에서 실행합니다.

```powershell
.\gradlew.bat test
```

DB 테스트는 Testcontainers로 별도의 PostgreSQL 15 컨테이너를 만들고
`init-postgresql.sql`로 초기화합니다. 로컬 `noticeboard` DB는 사용하지 않으며,
`testboard` DB를 수동으로 만들 필요가 없습니다. 최초 실행 시 PostgreSQL 이미지
다운로드가 필요할 수 있습니다.

### 스키마 동작

- 자동 증가 기본 키는 PostgreSQL `BIGSERIAL`을 사용합니다.
- 게시글 본문은 `TEXT`, 날짜와 시각은 `DATE` / `TIMESTAMP`를 사용합니다.
- `contents`, `comments`, `replies`, `products`의 `update_date`와
  `page_metadata`, `fcm_tokens`의 `updated_at`은 행이 변경되면 트리거로 갱신됩니다.
  쿼리에서 수정 시각을 다른 값으로 직접 지정한 경우에는 그 값을 유지합니다.
  ([PostgreSQL 트리거 문서](https://www.postgresql.org/docs/15/plpgsql-trigger.html))
- `fcm_tokens.user_id`는 NULL을 허용하며 외래 키를 두지 않습니다.
  `token`의 유일성 제약은 유지합니다.
