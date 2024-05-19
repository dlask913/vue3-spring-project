# backend

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
);
```