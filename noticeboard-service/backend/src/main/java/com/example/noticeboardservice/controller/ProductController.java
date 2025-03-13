package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.service.CategoryService;
import com.example.noticeboardservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productServiceImpl;
    private final CategoryService categoryServiceImpl;

    @PostMapping("/product")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "상품 등록 API")
    private ResponseEntity<String> createProduct(@RequestPart("productDto") ProductRequestDto productDto,
                                                 @RequestPart(value = "productImg", required = false) MultipartFile productImg){
        Long result = productServiceImpl.insertProduct(productDto, productImg);
        if (result <= 0){
            return ResponseEntity.badRequest().body("상품 등록에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("상품 등록에 성공하였습니다.");
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "상품 상세 조회 API")
    private ResponseEntity<ProductDetailsResponseDto> findProductById(@PathVariable("productId") Long productId) {
        ProductDetailsResponseDto product = productServiceImpl.findProduct(productId);
        return ResponseEntity.ok().body(product);
    }

    @PutMapping("/product/{productId}")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")}, summary = "상품 정보 수정 API")
    private ResponseEntity<String> updateProduct(@RequestBody ProductRequestDto updateDto) {
        int result = productServiceImpl.updateProduct(updateDto);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("상품 정보 수정에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("상품 수정에 성공하였습니다.");
    }

    @DeleteMapping("/productId/{productId}")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")}, summary = "상품 삭제 API")
    private ResponseEntity<String> deleteProduct(@PathVariable("productId") Long productId) {
        int result = productServiceImpl.deleteProduct(productId);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("삭제 실패하였습니다.");
        }
        return ResponseEntity.ok().body("삭제 완료되었습니다.");
    }

    @GetMapping("/products")
    @Operation(summary = "상품 검색 및 모두 조회 API")
    public ResponseEntity<List<ProductResponseDto>> searchNoticesByPage(
            @RequestParam(value = "sort", defaultValue = "post_date") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam Map<String, String> params
    ) {
        List<ProductResponseDto> products = productServiceImpl.searchProductsByKeyword(sort, order, params);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/categories")
    @Operation(summary = "카테고리 모든 값 조회 API")
    public ResponseEntity<?> getCategories() {
        List<CategoryDto> categories = categoryServiceImpl.findAllCategories();
        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/products/{category}")
    @Operation(summary = "카테고리별 상품 조회 API")
    private ResponseEntity<List<ProductResponseDto>> findProductsByCategory(@PathVariable("category") String category) {
        List<ProductResponseDto> products = productServiceImpl.findProductsByCategory(category);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/products/member/{memberId}")
    @Operation(summary = "회원이 등록한 상품 모두 조회 API")
    private ResponseEntity<List<ProductResponseDto>> findProductsByMemberId(@PathVariable("memberId") Long memberId) {
        List<ProductResponseDto> products = productServiceImpl.findProductsByMemberId(memberId);
        return ResponseEntity.ok().body(products);
    }
}
