package com.example.noticeboardservice.service.product;

import com.example.noticeboardservice.dto.product.CategoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryDto> findAllCategories();
}
