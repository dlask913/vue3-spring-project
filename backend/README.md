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
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255)
);

CREATE TABLE notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    post_date DATETIME NOT NULL,
    view_count BIGINT DEFAULT 0,
    member_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES member(id)
    ON DELETE CASCADE
);

CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    post_date DATETIME NOT NULL,
    member_id BIGINT,
    notice_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES member(id)
    ON DELETE CASCADE,
    FOREIGN KEY (notice_id) REFERENCES notice(id)
    ON DELETE CASCADE
);

CREATE TABLE heart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    comment_id BIGINT, 
    FOREIGN KEY (member_id) REFERENCES member(id)
    ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comment(id)
    ON DELETE CASCADE
);

CREATE TABLE images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_id BIGINT NOT ULL,
    img_name VARCHAR(255) NOT NULL,
    ori_img_name VARCHAR(255) NOT NULL,
    img_url VARCHAR(255) NOT NULL,
    image_type VARCHAR(20) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

```