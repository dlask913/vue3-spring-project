package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.CategoryDto;
import com.example.noticeboardservice.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAllCategories() {
        return categoryMapper.findAllCategories();
    }
}
