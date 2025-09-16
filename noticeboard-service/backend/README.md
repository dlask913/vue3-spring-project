# backend

### 개발 환경
- Build Tool: Gradle
- Framework: Spring Boot 3.2.5
- Programming Language: Java, JDK 17
- ORM: MyBatis 
- DBMS: MySQL

* * *
### DDL script
```sql
CREATE TABLE Members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255),
    address VARCHAR(255),
    user_type VARCHAR(20) NOT NULL DEFAULT 'USER'
);

CREATE TABLE Notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content MEDIUMTEXT NOT NULL,
    is_html CHAR NOT NULL DEFAULT 'N',
    post_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    view_count BIGINT DEFAULT 0,
    member_id BIGINT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES Members(id) ON DELETE CASCADE
);

CREATE TABLE Comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    post_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    member_id BIGINT NOT NULL,
    notice_id BIGINT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES Members(id) ON DELETE CASCADE,
    FOREIGN KEY (notice_id) REFERENCES Notices(id) ON DELETE CASCADE
);

CREATE TABLE Hearts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    comment_id BIGINT, 
    FOREIGN KEY (member_id) REFERENCES Members(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES Comments(id) ON DELETE CASCADE
);

CREATE TABLE Files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    ori_file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Replies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    post_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    comment_id BIGINT,
    member_id BIGINT,
    FOREIGN KEY (comment_id) REFERENCES Comments(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES Members(id) ON DELETE CASCADE
);

CREATE TABLE Products (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL, 
	content VARCHAR(255) NOT NULL,
    standard_price INT NOT NULL DEFAULT 0,
    category VARCHAR(20) NOT NULL,
    deadline DATE NOT NULL,
	post_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	owner_id BIGINT,
	FOREIGN KEY (owner_id) REFERENCES Members(id) ON DELETE CASCADE
);

CREATE TABLE Bid_History (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bid_price INT,
    customer_id BIGINT,
    product_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Members(id),
    FOREIGN KEY (product_id) REFERENCES Products(id)
);

CREATE INDEX idx_bid_productid_bidprice ON Bid_History (product_id, bid_price DESC);

CREATE TABLE Categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL, 
    description VARCHAR(50) NOT NULL
);

CREATE TABLE Addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    address_name VARCHAR(255),
    road_address_name VARCHAR(255),
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES Members(id) ON DELETE CASCADE
);

CREATE TABLE Messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content VARCHAR(255),
    is_read CHAR DEFAULT 'N',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id_lower BIGINT NOT NULL,
    member_id_higher BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (member_id_lower, member_id_higher) -- 동일한 두 사람 간 중복된 방 생성 방지
);

CREATE INDEX idx_messages_room_created ON Messages (room_id, created_at DESC);

-- 메일 보낸 이력을 저장할 로그 테이블 생성
CREATE TABLE Mail_Send_Log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver_id BIGINT NOT NULL,
    receiver_email VARCHAR(50) NOT NULL,
    template VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Refresh Token 저장할 테이블 생성
CREATE TABLE Refresh_Token (
    username VARCHAR(255) PRIMARY KEY,
    token TEXT NOT NULL,
    expiry_date TIMESTAMP NOT NULL
);

```