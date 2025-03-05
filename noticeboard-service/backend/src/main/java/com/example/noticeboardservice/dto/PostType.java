package com.example.noticeboardservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum PostType {
    PRODUCT("상품"), NOTICE("게시글");

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
