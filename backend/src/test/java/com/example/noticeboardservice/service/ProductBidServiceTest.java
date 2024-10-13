package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.ProductBidHistoryMapper;
import com.example.noticeboardservice.mapper.ProductMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(value = "test")
class ProductBidServiceTest {
    @Autowired
    ProductBidService productBidServiceImpl;
    @Autowired
    ProductBidHistoryMapper productBidHistoryMapper;

    @Autowired
    ProductMapper productMapper;
    @Autowired
    MemberMapper memberMapper;

    @AfterEach
    void tearDown() {
        productBidHistoryMapper.deleteAll();
        productMapper.deleteAll();
        memberMapper.deleteAll();
    }

    @Test
    @DisplayName("원하는 상품에 입찰하고싶은 가격을 등록한다.")
    void addBidHistory() {
        // given
        MemberResponseDto member = getMember("buyer@test.com"); // 고객
        ProductResponseDto product = getProduct(2000, "owner@test.com");
        ProductBidDto productBidDto = ProductBidDto.builder()
                .bidPrice(3000)
                .clientEmail(member.email())
                .productId(product.id())
                .build();

        // when
        productBidServiceImpl.addBidHistory(productBidDto);

        // then
        ProductBidDto findProductBid = productBidHistoryMapper.findAllBidHistories().get(0);
        Assertions.assertThat(findProductBid.getProductId()).isEqualTo(productBidDto.getProductId());
        Assertions.assertThat(findProductBid.getBidPrice()).isEqualTo(productBidDto.getBidPrice());
        Assertions.assertThat(findProductBid.getClientEmail()).isEqualTo(productBidDto.getClientEmail());

    }

    private ProductResponseDto getProduct(int standardPrice, String ownerEmail) {
        MemberResponseDto member = getMember(ownerEmail);
        ProductRequestDto productDto = ProductRequestDto.builder()
                .title("title")
                .content("content")
                .standardPrice(standardPrice)
                .ownerId(member.id())
                .build();
        productMapper.insertProduct(productDto);
        return productMapper.findAllProducts().get(0);
    }

    private MemberResponseDto getMember(String email){
        MemberResponseDto findMember = memberMapper.findByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberMapper.findByEmail(email);
        }
        return findMember;
    }
}