package com.limnj.noticeboardadmin.category;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryServiceImpl;

    @PostMapping("/category")
    @Operation(summary = "카테고리 생성 API")
    public ResponseEntity<String> saveCategory(@RequestPart("categoryDto") CategoryDto categoryDto,
                                               @RequestPart(value = "categoryImg") MultipartFile categoryImg) {
        int result = categoryServiceImpl.insertCategory(categoryDto, categoryImg);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("카테고리 생성에 실패하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("카테고리 생성이 완료되었습니다.");
    }

    @DeleteMapping("/category/{categoryId}")
    @Operation(summary = "카테고리 삭제 API")
    public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        int result = categoryServiceImpl.deleteCategory(categoryId);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("카테고리 삭제에 실패하였습니다.");
        }
        return ResponseEntity.noContent().build();
    }
}
