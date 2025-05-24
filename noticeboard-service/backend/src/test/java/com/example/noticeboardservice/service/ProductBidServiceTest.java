package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.exception.BidPriceBelowCurrentException;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.ProductBidHistoryMapper;
import com.example.noticeboardservice.mapper.ProductMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
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
    void addBidHistoryTest(){
        // given
        MemberResponseDto member = getMember("buyer@test.com"); // 고객
        Long productId = getProductId(2000, "owner@test.com");
        ProductBidDto productBidDto = createProductBidDto(3000, member.id(), productId);

        // when
        productBidServiceImpl.addBidHistory(productBidDto);

        // then
        ProductBidDto findProductBid = productBidHistoryMapper.findLatestBidHistory(productId);
        assertThat(findProductBid.getProductId()).isEqualTo(productBidDto.getProductId());
        assertThat(findProductBid.getBidPrice()).isEqualTo(productBidDto.getBidPrice());
        assertThat(findProductBid.getCustomerId()).isEqualTo(productBidDto.getCustomerId());
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
    @DisplayName("동시에 여러 가격을 입력할 경우, 최신 가격은 가장 높은 가격이어야 한다.")
    void addBidPriceConcurrencyTest() throws InterruptedException{
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        int standardPrice = 2000;
        Long productId = getProductId(standardPrice, "owner@test.com");
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            MemberResponseDto member = getMember("buyer"+i+"@test.com"); // 고객
            int bidPrice = standardPrice + i + 1;
            executorService.submit(() -> {
                try {
                    ProductBidDto productBidDto = createProductBidDto(bidPrice, member.id(), productId);
                    productBidServiceImpl.addBidHistory(productBidDto);
                } finally {
                    latch.countDown(); // 다른 스레드에서 수행되는 작업이 완료될 때까지 대기
                }
            });
        }

        latch.await();

        // then
        ProductBidDto findProductBid = productBidHistoryMapper.findLatestBidHistory(productId);
        assertThat(findProductBid.getBidPrice()).isEqualTo(standardPrice + threadCount); // standardPrice + threadCount = 가장 높은 금액
    }

    @Test
    @DisplayName("동시에 같은 가격을 입력할 경우, 입찰 조회 결과는 하나여야 한다.")
    void addBidPriceUniqueTest() throws InterruptedException{
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        int standardPrice = 2000;
        int bidPrice = standardPrice + threadCount;
        Long productId = getProductId(standardPrice, "owner@test.com");
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            MemberResponseDto member = getMember("buyer"+i+"@test.com"); // 고객
            executorService.submit(() -> {
                try {
                    ProductBidDto productBidDto = createProductBidDto(bidPrice, member.id(), productId);
                    productBidServiceImpl.addBidHistory(productBidDto);
                } finally {
                    latch.countDown(); // 다른 스레드에서 수행되는 작업이 완료될 때까지 대기
                }
            });
        }

        latch.await();

        // then
        ProductBidDto findProductBid = productBidHistoryMapper.findLatestBidHistory(productId);
        assertThat(findProductBid.getBidPrice()).isEqualTo(bidPrice);
    }

    @Test
    @DisplayName("최종 입찰된 결과를 조회한다.")
    void findLatestBidHistoryTest(){
        // given
        MemberResponseDto member1= getMember("buyer@test.com"); // 고객
        MemberResponseDto member2 = getMember("buyer2@test.com"); // 최종 입찰한 고객
        Long productId = getProductId(2000, "owner@test.com");
        ProductBidDto productBidDto1 =  createProductBidDto(3000, member1.id(), productId);
        ProductBidDto productBidDto2 =  createProductBidDto(4000, member1.id(), productId);
        ProductBidDto productBidDto3 =  createProductBidDto(5000, member2.id(), productId);
        productBidServiceImpl.addBidHistory(productBidDto1);
        productBidServiceImpl.addBidHistory(productBidDto2);
        productBidServiceImpl.addBidHistory(productBidDto3);

        // when
        ProductBidDto bidResultDto = productBidServiceImpl.findLatestBidHistory(productId);

        // then
        assertThat(bidResultDto.getProductId()).isEqualTo(productBidDto3.getProductId());
        assertThat(bidResultDto.getBidPrice()).isEqualTo(productBidDto3.getBidPrice());
        assertThat(bidResultDto.getCustomerId()).isEqualTo(productBidDto3.getCustomerId());
        assertThat(bidResultDto.getCustomerEmail()).isEqualTo(member2.email());
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