-- 1. members 테이블
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255),
    address VARCHAR(255),
    secret_key VARCHAR(50),
    user_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    login_fail_count INT DEFAULT 0,
    lock_until TIMESTAMP DEFAULT NULL
);

-- 2. contents 테이블
CREATE TABLE contents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_html CHAR(1) NOT NULL DEFAULT 'N',
    post_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    view_count BIGINT DEFAULT 0,
    member_id BIGINT NOT NULL,
    post_type VARCHAR(50) NOT NULL,
    CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- 3. comments 테이블
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    post_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    hide_yn VARCHAR(1) NOT NULL DEFAULT 'N',
    member_id BIGINT NOT NULL,
    notice_id BIGINT NOT NULL,
    CONSTRAINT fk_member_comm FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_notice_comm FOREIGN KEY (notice_id) REFERENCES contents(id) ON DELETE CASCADE
);

-- 4. hearts 테이블
CREATE TABLE hearts (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT,
    comment_id BIGINT,
    CONSTRAINT fk_member_heart FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_heart FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);

-- 5. files 테이블
CREATE TABLE files (
    id BIGSERIAL PRIMARY KEY,
    type_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    ori_file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. replies 테이블
CREATE TABLE replies (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment_id BIGINT,
    member_id BIGINT,
    CONSTRAINT fk_comment_reply FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    CONSTRAINT fk_member_reply FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- 7. products 테이블
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    standard_price INT NOT NULL DEFAULT 0,
    category VARCHAR(20) NOT NULL,
    deadline DATE NOT NULL,
    post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id BIGINT,
    CONSTRAINT fk_owner_prod FOREIGN KEY (owner_id) REFERENCES members(id) ON DELETE CASCADE
);

-- 8. bid_history 테이블 및 인덱스
CREATE TABLE bid_history (
    id BIGSERIAL PRIMARY KEY,
    bid_price INT,
    customer_id BIGINT,
    product_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_bid FOREIGN KEY (customer_id) REFERENCES members(id),
    CONSTRAINT fk_product_bid FOREIGN KEY (product_id) REFERENCES products(id)
);
CREATE INDEX idx_bid_productid_bidprice ON bid_history (product_id, bid_price DESC);

-- 9. categories 테이블
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(50) NOT NULL
);

-- 10. addresses 테이블
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    address_name VARCHAR(255),
    road_address_name VARCHAR(255),
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    CONSTRAINT fk_member_addr FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- 11. messages 테이블 및 인덱스
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content VARCHAR(255),
    is_read CHAR(1) DEFAULT 'N',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_messages_room_created ON messages (room_id, created_at DESC);

-- 12. rooms 테이블
CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    member_id_lower BIGINT NOT NULL,
    member_id_higher BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (member_id_lower, member_id_higher)
);

-- 13. mail_send_log 테이블
CREATE TABLE mail_send_log (
    id BIGSERIAL PRIMARY KEY,
    receiver_id BIGINT NOT NULL,
    receiver_email VARCHAR(50) NOT NULL,
    template VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. refresh_token 테이블
CREATE TABLE refresh_token (
    username VARCHAR(255) PRIMARY KEY,
    token TEXT NOT NULL,
    expiry_date TIMESTAMP NOT NULL
);

-- 15. access_log 테이블
CREATE TABLE access_log (
    id BIGSERIAL PRIMARY KEY,
    method VARCHAR(10),
    uri VARCHAR(255),
    client_ip VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 16. product_order 테이블
CREATE TABLE product_order (
    product_order_id BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    order_qty BIGINT NOT NULL,
    unit_price INT NOT NULL,
    order_date VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON COLUMN product_order.product_code IS '상품 코드';
COMMENT ON COLUMN product_order.product_name IS '상품명';

-- 17. page_metadata 테이블
CREATE TABLE page_metadata (
    page_id BIGSERIAL PRIMARY KEY,
    domain VARCHAR(50) NOT NULL,
    menu_name VARCHAR(100) NOT NULL,
    page_url VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_page_url UNIQUE (page_url)
);
COMMENT ON COLUMN page_metadata.domain IS '기능 도메인 (예: 공지, 인증, 보안, 통계, 재고)';
COMMENT ON COLUMN page_metadata.menu_name IS '화면/메뉴 이름';
COMMENT ON COLUMN page_metadata.page_url IS '프론트엔드 라우팅 경로';
COMMENT ON COLUMN page_metadata.description IS '페이지 의도 설명 (Embedding 대상)';

-- 18. audit_log 테이블
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    user_id BIGINT NULL,
    username VARCHAR(100) NULL,
    ip_address VARCHAR(45) NULL,
    action_type VARCHAR(20) NULL,
    is_success BOOLEAN NOT NULL DEFAULT TRUE,
    message VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE audit_log IS '감사 및 보안 로그 테이블';
COMMENT ON COLUMN audit_log.event_type IS 'LOGIN, REGISTER_MEMBER, AUTH, ERROR';
COMMENT ON COLUMN audit_log.user_id IS '회원 ID (비로그인 가능)';
COMMENT ON COLUMN audit_log.username IS '회원 닉네임';
COMMENT ON COLUMN audit_log.ip_address IS 'IP주소';
COMMENT ON COLUMN audit_log.action_type IS 'CREATE, UPDATE, DELETE';
COMMENT ON COLUMN audit_log.is_success IS '성공 여부';
COMMENT ON COLUMN audit_log.message IS '내용';

-- 19. fcm_tokens 테이블
CREATE TABLE fcm_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    token VARCHAR(512) NOT NULL,
    device_type VARCHAR(20) DEFAULT 'WEB',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_fcm_token UNIQUE (token)
);