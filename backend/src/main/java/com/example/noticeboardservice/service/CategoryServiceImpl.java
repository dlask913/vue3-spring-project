package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.CategoryDto;
import com.example.noticeboardservice.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryMapper categoryMapper;

    @Override
    public int insertCategory(CategoryDto categoryDto) {
        return categoryMapper.insertCategory(categoryDto);
    }

    @Override
    public int deleteCategory(Long id) {
        return categoryMapper.deleteCategory(id);
    }
}
