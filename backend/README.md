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
CREATE TABLE Member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    post_date DATETIME NOT NULL,
    member_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES Member(id)
    ON DELETE CASCADE
);

CREATE TABLE Comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    post_date DATETIME NOT NULL,
    is_like BIGINT DEFAULT 0 NOT NULL, 
    member_id BIGINT,
    notice_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES Member(id)
    ON DELETE CASCADE,
    FOREIGN KEY (notice_id) REFERENCES Notice(id)
    ON DELETE CASCADE
);
```