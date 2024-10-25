package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * category 조회 시 요칭 및 응답 용도
 */
@Getter
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String description;

    @Builder
    public CategoryDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
