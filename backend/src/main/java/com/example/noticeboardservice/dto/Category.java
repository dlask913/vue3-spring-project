package com.example.noticeboardservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

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

    @JsonCreator // 역 직렬화
    public static Category toCategory(String value) {
        for (Category category : Category.values()) {
            if (category.getValue().equals(value)) {
                return category;
            }
        }
        return null;
    }

    @JsonValue // 직렬화
    public String getValue() {
        return description;
    }
}
