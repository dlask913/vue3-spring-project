package com.example.noticeboardservice.controller.product;

import com.example.noticeboardservice.dto.product.ProductBidDto;
import com.example.noticeboardservice.service.product.ProductBidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductBidController {
    private final ProductBidService productBidServiceImpl;

    @PostMapping("/bid")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "입찰하고 싶은 상품에 가격 등록하기")
    public ResponseEntity<String> createProductBidPrice(@RequestBody ProductBidDto productBidDto){
        int result = productBidServiceImpl.addBidHistory(productBidDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("가격 등록에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("가격 등록에 성공하였습니다.");
    }
}
