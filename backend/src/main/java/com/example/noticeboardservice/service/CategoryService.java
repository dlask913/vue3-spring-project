package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.CategoryDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface CategoryService {
    int insertCategory(CategoryDto categoryDto, MultipartFile categoryImg);
    int deleteCategory(Long id);
}
