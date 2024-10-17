package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import com.example.noticeboardservice.dto.ProductRequestDto;
import com.example.noticeboardservice.dto.ProductResponseDto;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.ProductMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles(value = "test")
class ProductServiceTest {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductService productServiceImpl;

    @Autowired
    MemberMapper memberMapper;

    @AfterEach
    void tearDown() {
        productMapper.deleteAll();
    }

    @Test
    @DisplayName("판매할 상품을 등록한다.")
    void insertProduct() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", 1000, member.id());

        // when
        productServiceImpl.insertProduct(requestDto, null);

        // then
        ProductResponseDto findProduct = productMapper.findAllProducts().get(0);
        Assertions.assertThat(findProduct.title()).isEqualTo(requestDto.getTitle());
        Assertions.assertThat(findProduct.content()).isEqualTo(requestDto.getContent());
        Assertions.assertThat(findProduct.standardPrice()).isEqualTo(requestDto.getStandardPrice());
        Assertions.assertThat(findProduct.ownerId()).isEqualTo(requestDto.getOwnerId());
    }

    @Test
    @DisplayName("판매할 상품의 제목, 내용, 가격을 수정한다.")
    void updateProduct() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", 1000, member.id());
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findAllProducts().get(0).id();
        ProductRequestDto updateDto = createProductRequestDto(
                productId, "고양이 장난감 팔아요", "강아지 장난감은 다 팔렸어요", 220, requestDto.getOwnerId());

        // when
        productServiceImpl.updateProduct(updateDto);

        // then
        ProductResponseDto findProduct = productMapper.findAllProducts().get(0);
        Assertions.assertThat(findProduct.title()).isEqualTo(updateDto.getTitle());
        Assertions.assertThat(findProduct.content()).isEqualTo(updateDto.getContent());
        Assertions.assertThat(findProduct.standardPrice()).isEqualTo(updateDto.getStandardPrice());
        Assertions.assertThat(findProduct.ownerId()).isEqualTo(updateDto.getOwnerId());
    }

    @Test
    @DisplayName("등록한 상품을 삭제한다.")
    void deleteProduct() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", 1000, member.id());
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findAllProducts().get(0).id();

        // when
        productServiceImpl.deleteProduct(productId);

        // then
        Assertions.assertThat(productMapper.findAllProducts().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 상품을 단일 조회한다.")
    void findProduct() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", 1000, member.id());
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findAllProducts().get(0).id();

        // when
        ProductResponseDto findProduct = productServiceImpl.findProduct(productId);

        // then
        Assertions.assertThat(findProduct.title()).isEqualTo(requestDto.getTitle());
        Assertions.assertThat(findProduct.content()).isEqualTo(requestDto.getContent());
        Assertions.assertThat(findProduct.standardPrice()).isEqualTo(requestDto.getStandardPrice());
        Assertions.assertThat(findProduct.ownerId()).isEqualTo(requestDto.getOwnerId());
    }

    private static ProductRequestDto createProductRequestDto(Long productId, String title, String content, int price, Long memberId) {
        return ProductRequestDto.builder()
                .id(productId)
                .title(title)
                .content(content)
                .standardPrice(price)
                .ownerId(memberId)
                .build();
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