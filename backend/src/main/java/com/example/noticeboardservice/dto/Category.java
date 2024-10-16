package com.example.noticeboardservice.dto;

import lombok.Getter;

@Getter
public enum Category {
    ELECTRONICS("전자제품"),
    FASHION("패션/의류"),
    FURNITURE("가구/인테리어"),
    HOBBIES("취미/레저"),
    BOOKS_MEDIA("도서/음반/영화");

    private final String description;

    Category(String description) {
        this.description = description;
    }
}
