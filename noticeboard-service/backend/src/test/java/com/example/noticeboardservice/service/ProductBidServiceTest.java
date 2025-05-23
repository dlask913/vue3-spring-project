package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.exception.BidPriceBelowCurrentException;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(value = "test")
class ProductBidServiceTest {
    @Autowired
    ProductBidService productBidServiceImpl;
    @Autowired
    ProductBidHistoryMapper productBidHistoryMapper;

    @Autowired
    ProductService productServiceImpl;
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
    void addBidHistoryTest() throws InterruptedException {
        // given
        MemberResponseDto member = getMember("buyer@test.com"); // 고객
        Long productId = getProductId(2000, "owner@test.com");
        ProductBidDto productBidDto = createProductBidDto(3000, member.id(), productId);

        Thread.sleep(1000); // 히스토리 간 간격 주기

        // when
        productBidServiceImpl.addBidHistory(productBidDto);

        // then
        ProductBidDto findProductBid = productBidHistoryMapper.findLatestBidHistory(productId);
        Assertions.assertThat(findProductBid.getProductId()).isEqualTo(productBidDto.getProductId());
        Assertions.assertThat(findProductBid.getBidPrice()).isEqualTo(productBidDto.getBidPrice());
        Assertions.assertThat(findProductBid.getCustomerId()).isEqualTo(productBidDto.getCustomerId());
    }

    @Test
    @DisplayName("가장 최신의 가격보다 낮은 가격은 입력이 불가능하다.")
    void addBidPriceBelowCurrentTest() {
        // given
        MemberResponseDto member = getMember("buyer@test.com"); // 고객
        Long productId = getProductId(2000, "owner@test.com");
        ProductBidDto productBidDto =  createProductBidDto(1500, member.id(), productId);

        // when // then
        assertThrows(BidPriceBelowCurrentException.class, () -> {
            productBidServiceImpl.addBidHistory(productBidDto);
        }, "현재 가격보다 높은 가격을 입력해주세요.");
    }

    @Test
    @DisplayName("최종 입찰된 결과를 조회한다.")
    void findLatestBidHistoryTest() throws InterruptedException {
        // given
        MemberResponseDto member1= getMember("buyer@test.com"); // 고객
        MemberResponseDto member2 = getMember("buyer2@test.com"); // 최종 입찰한 고객
        Long productId = getProductId(2000, "owner@test.com");
        ProductBidDto productBidDto1 =  createProductBidDto(3000, member1.id(), productId);
        ProductBidDto productBidDto2 =  createProductBidDto(4000, member1.id(), productId);
        ProductBidDto productBidDto3 =  createProductBidDto(5000, member2.id(), productId);
        productBidServiceImpl.addBidHistory(productBidDto1);
        Thread.sleep(1000); // 히스토리 간 간격 주기
        productBidServiceImpl.addBidHistory(productBidDto2);
        Thread.sleep(1000); // 히스토리 간 간격 주기
        productBidServiceImpl.addBidHistory(productBidDto3);
        Thread.sleep(1000); // 히스토리 간 간격 주기

        // todo: 간격 없이 동시 요청하면 실패 → 쿼리 수정

        // when
        ProductBidDto bidResultDto = productBidServiceImpl.findLatestBidHistory(productId);

        // then
        Assertions.assertThat(bidResultDto.getProductId()).isEqualTo(productBidDto3.getProductId());
        Assertions.assertThat(bidResultDto.getBidPrice()).isEqualTo(productBidDto3.getBidPrice());
        Assertions.assertThat(bidResultDto.getCustomerId()).isEqualTo(productBidDto3.getCustomerId());
        Assertions.assertThat(bidResultDto.getCustomerEmail()).isEqualTo(member2.email());
    }

    private Long getProductId(int standardPrice, String ownerEmail) {
        MemberResponseDto member = getMember(ownerEmail);
        ProductRequestDto productDto = ProductRequestDto.builder()
                .title("title")
                .content("content")
                .standardPrice(standardPrice)
                .category("FURNITURE")
                .ownerId(member.id())
                .build();
        productServiceImpl.insertProduct(productDto, null); // 초기 bidHistory 생성
        return productDto.getId();
    }

    private MemberResponseDto getMember(String email){
        MemberResponseDto findMember = memberMapper.findMemberByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberMapper.findMemberByEmail(email);
        }
        return findMember;
    }

    private static ProductBidDto createProductBidDto(int bidPrice, Long customerId, Long productId) {
        return ProductBidDto.builder()
                .bidPrice(bidPrice)
                .customerId(customerId)
                .productId(productId)
                .build();
    }
}