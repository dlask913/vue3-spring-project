package com.example.noticeboardservice.dto.notice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum PostType {
    PRODUCT("상품"), POST("게시글"), NOTICE("공지사항");

    private final String value;

    PostType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PostType toPostType(String value){
        for (PostType type : PostType.values()) {
            if(type.getValue().equals(value))
                return type;
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
