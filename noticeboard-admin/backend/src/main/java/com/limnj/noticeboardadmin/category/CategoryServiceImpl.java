package com.limnj.noticeboardadmin.category;

import com.limnj.noticeboardadmin.file.FileInfoRequestDto;
import com.limnj.noticeboardadmin.file.FileInfoService;
import com.limnj.noticeboardadmin.file.FileType;
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
    private final FileInfoService fileInfoService;
    @Value("${categoryImgLocation}")
    private String categoryImgLocation;

    @Override
    public int insertCategory(CategoryDto categoryDto, MultipartFile categoryImg) {
        int result = categoryMapper.insertCategory(categoryDto);

        FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                .typeId(categoryDto.getId())
                .fileType(FileType.CATEGORY)
                .build();
        fileInfoService.saveFile(fileInfoRequestDto, categoryImg, categoryImgLocation);

        return result;
    }

    @Override
    public int deleteCategory(Long categoryId) {
        fileInfoService.findByTypeId(categoryId, FileType.CATEGORY)
                .ifPresent(fileDto -> fileInfoService.deleteFile(
                        fileDto.getId(), categoryImgLocation, fileDto.getFileName()));
        return categoryMapper.deleteCategory(categoryId);
    }

    @Override
    public List<CategoryDto> findAllCategories() {
        return categoryMapper.findAllCategories();
    }
}
