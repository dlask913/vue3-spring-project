package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.CategoryDto;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {
    int insertCategory(CategoryDto categoryDto);
    int deleteCategory(Long id);
}
