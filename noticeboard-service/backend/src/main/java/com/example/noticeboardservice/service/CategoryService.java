package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.CategoryDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryDto> findAllCategories();
}
