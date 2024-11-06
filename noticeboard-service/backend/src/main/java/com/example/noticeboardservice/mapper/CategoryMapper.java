package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.CategoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryDto> findAllCategories();
}