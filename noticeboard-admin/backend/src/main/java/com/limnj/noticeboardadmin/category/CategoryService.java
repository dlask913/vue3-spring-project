package com.limnj.noticeboardadmin.category;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CategoryService {
    int insertCategory(CategoryDto categoryDto, MultipartFile categoryImg);
    int deleteCategory(Long id);
    List<CategoryDto> findAllCategories();
}
