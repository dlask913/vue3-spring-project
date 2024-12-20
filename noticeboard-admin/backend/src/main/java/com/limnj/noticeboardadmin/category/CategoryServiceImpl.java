package com.limnj.noticeboardadmin.category;

import com.limnj.noticeboardadmin.image.ImageRequestDto;
import com.limnj.noticeboardadmin.image.ImageService;
import com.limnj.noticeboardadmin.image.ImageType;
import com.limnj.noticeboardadmin.category.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service @Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryMapper categoryMapper;
    private final ImageService imageServiceImpl;
    @Value("${categoryImgLocation}")
    private String categoryImgLocation;

    @Override
    public int insertCategory(CategoryDto categoryDto, MultipartFile categoryImg) {
        int result = categoryMapper.insertCategory(categoryDto);

        ImageRequestDto imageRequestDto = ImageRequestDto.builder()
                .typeId(categoryDto.getId())
                .imageType(ImageType.CATEGORY)
                .build();
        imageServiceImpl.saveImage(imageRequestDto, categoryImg, categoryImgLocation);

        return result;
    }

    @Override
    public int deleteCategory(Long categoryId) {
        imageServiceImpl.findByTypeId(categoryId, ImageType.CATEGORY)
                .ifPresent(imageDto -> imageServiceImpl.deleteImage(
                        imageDto.id(), categoryImgLocation, imageDto.imgName()));
        return categoryMapper.deleteCategory(categoryId);
    }

    @Override
    public List<CategoryDto> findAllCategories() {
        return categoryMapper.findAllCategories();
    }
}
