package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.CategoryDto;
import com.example.noticeboardservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryServiceImpl;

    @PostMapping("/category")
    @Operation(summary = "카테고리 생성 API")
    public ResponseEntity<String> saveCategory(@RequestBody CategoryDto categoryDto) {
        int result = categoryServiceImpl.insertCategory(categoryDto);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("카테고리 생성에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("카테고리 생성이 완료되었습니다.");
    }

    @DeleteMapping("/category/{categoryId}")
    @Operation(summary = "카테고리 삭제 API")
    public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        int result = categoryServiceImpl.deleteCategory(categoryId);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("카테고리 삭제에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("카테고리 삭제 완료되었습니다.");
    }
}
