# backend

### 개발 환경

- Build Tool: Gradle
- Framework: Spring Boot 3.2.5
- Programming Language: Java, JDK 17
- ORM: MyBatis
- DBMS: MySQL

---

### DDL script

```sql
CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255),
    address VARCHAR(255),
    secret_key VARCHAR(50),
    user_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content MEDIUMTEXT NOT NULL,
    is_html CHAR NOT NULL DEFAULT 'N',
    post_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    view_count BIGINT DEFAULT 0,
    member_id BIGINT NOT NULL,
    post_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    post_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    hide_yn VARCHAR(1) NOT NULL DEFAULT 'N',
    member_id BIGINT NOT NULL,
    notice_id BIGINT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    FOREIGN KEY (notice_id) REFERENCES contents(id) ON DELETE CASCADE
);

CREATE TABLE hearts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    comment_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TABLE files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    ori_file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE replies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    post_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    comment_id BIGINT,
    member_id BIGINT,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE products (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
	content VARCHAR(255) NOT NULL,
    standard_price INT NOT NULL DEFAULT 0,
    category VARCHAR(20) NOT NULL,
    deadline DATE NOT NULL,
	post_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	owner_id BIGINT,
	FOREIGN KEY (owner_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE bid_history (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bid_price INT,
    customer_id BIGINT,
    product_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES members(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_bid_productid_bidprice ON bid_history (product_id, bid_price DESC);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(50) NOT NULL
);

CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    address_name VARCHAR(255),
    road_address_name VARCHAR(255),
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content VARCHAR(255),
    is_read CHAR DEFAULT 'N',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id_lower BIGINT NOT NULL,
    member_id_higher BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (member_id_lower, member_id_higher) -- 동일한 두 사람 간 중복된 방 생성 방지
);

CREATE INDEX idx_messages_room_created ON messages (room_id, created_at DESC);

-- 메일 보낸 이력을 저장할 로그 테이블 생성
CREATE TABLE mail_send_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver_id BIGINT NOT NULL,
    receiver_email VARCHAR(50) NOT NULL,
    template VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Refresh Token 저장할 테이블 생성
CREATE TABLE refresh_token (
    username VARCHAR(255) PRIMARY KEY,
    token TEXT NOT NULL,
    expiry_date TIMESTAMP NOT NULL
);

-- 접근 로그 저장할 테이블 생성
CREATE TABLE access_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    method VARCHAR(10),
    uri VARCHAR(255),
    client_ip VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_order (
    product_order_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL COMMENT '상품 코드',
    product_name VARCHAR(100) NOT NULL COMMENT '상품명',
    order_qty BIGINT NOT NULL,
    unit_price INT NOT NULL,
    order_date VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
)

CREATE TABLE page_metadata (
    page_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    domain      VARCHAR(50)  NOT NULL COMMENT '기능 도메인 (예: 공지, 인증, 보안, 통계, 재고)',
    menu_name   VARCHAR(100) NOT NULL COMMENT '화면/메뉴 이름',
    page_url    VARCHAR(255) NOT NULL COMMENT '프론트엔드 라우팅 경로',
    description TEXT NOT NULL COMMENT '페이지 의도 설명 (Embedding 대상)',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_page_url (page_url)
)

CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL COMMENT 'LOGIN, REGISTER_MEMBER, AUTH, ERROR',
    user_id BIGINT NULL COMMENT '회원 ID (비로그인 가능)',
    username VARCHAR(100) NULL COMMENT '회원 닉네임',
    ip_address VARCHAR(45) NULL COMMENT 'IP주소',
    action_type VARCHAR(20) NULL COMMENT 'CREATE, UPDATE, DELETE',
    is_success BOOLEAN NOT NULL DEFAULT TRUE COMMENT '성공 여부',
    message VARCHAR(500) NULL COMMENT '내용',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT='감사 및 보안 로그 테이블';
```
