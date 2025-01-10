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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles(value = "test")
class ProductServiceTest {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductService productServiceImpl;

    @Autowired
    ProductBidHistoryMapper productBidHistoryMapper;

    @Autowired
    MemberMapper memberMapper;

    @AfterEach
    void tearDown() {
        productBidHistoryMapper.deleteAll();
        productMapper.deleteAll();
    }

    @Test
    @DisplayName("판매할 상품을 이미지 없이 등록한다.")
    void insertProductWithoutImageTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id());

        // when
        productServiceImpl.insertProduct(requestDto, null);

        // then
        ProductResponseDto findProduct = productMapper.findAllProducts().get(0);

        Assertions.assertThat(findProduct.title()).isEqualTo(requestDto.getTitle());
        Assertions.assertThat(findProduct.content()).isEqualTo(requestDto.getContent());
        Assertions.assertThat(findProduct.standardPrice()).isEqualTo(requestDto.getStandardPrice());
        Assertions.assertThat(findProduct.ownerId()).isEqualTo(requestDto.getOwnerId());
        Assertions.assertThat(findProduct.imgUrl()).isEqualTo("/image/productDefaultImg.jpg");
    }

    @Test
    @DisplayName("상품을 등록하면 초기 가격 히스토리가 등록된다.")
    void checkPriceWhenProductInsertTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id());

        // when
        productServiceImpl.insertProduct(requestDto, null);

        // then
        ProductBidDto latestBidHistory = productBidHistoryMapper.findLatestBidHistory(requestDto.getId());
        Assertions.assertThat(latestBidHistory.getProductId()).isEqualTo(requestDto.getId());
        Assertions.assertThat(latestBidHistory.getBidPrice()).isEqualTo(requestDto.getStandardPrice());
        Assertions.assertThat(latestBidHistory.getCustomerId()).isEqualTo(requestDto.getOwnerId());
    }


    @Test
    @DisplayName("판매할 상품을 이미지와 함께 등록한다.")
    void insertProductWithImageTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id());
        MockMultipartFile productImg = new MockMultipartFile(
                "상품 이미지",
                "productImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "productImg!".getBytes()
        );

        // when
        productServiceImpl.insertProduct(requestDto, productImg);

        // then
        ProductResponseDto findProduct = productMapper.findAllProducts().get(0);
        Assertions.assertThat(findProduct.title()).isEqualTo(requestDto.getTitle());
        Assertions.assertThat(findProduct.content()).isEqualTo(requestDto.getContent());
        Assertions.assertThat(findProduct.standardPrice()).isEqualTo(requestDto.getStandardPrice());
        Assertions.assertThat(findProduct.ownerId()).isEqualTo(requestDto.getOwnerId());
        Assertions.assertThat(findProduct.imgUrl()).startsWith("/images/product/");
    }

    @Test
    @DisplayName("판매할 상품의 제목, 내용, 가격을 수정한다.")
    void updateProductTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id());
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findAllProducts().get(0).id();
        ProductRequestDto updateDto = createProductRequestDto(
                productId, "고양이 장난감 팔아요", "강아지 장난감은 다 팔렸어요", "FURNITURE", 220, requestDto.getOwnerId());

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
    void deleteProductTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id());
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findAllProducts().get(0).id();

        // when
        productServiceImpl.deleteProduct(productId);

        // then
        Assertions.assertThat(productMapper.findAllProducts().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 상품을 단일 조회한다.")
    void findProductTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id());
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findAllProducts().get(0).id();

        // when
        ProductDetailsResponseDto findProduct = productServiceImpl.findProduct(productId);

        // then
        Assertions.assertThat(findProduct.title()).isEqualTo(requestDto.getTitle());
        Assertions.assertThat(findProduct.content()).isEqualTo(requestDto.getContent());
        Assertions.assertThat(findProduct.standardPrice()).isEqualTo(requestDto.getStandardPrice());
        Assertions.assertThat(findProduct.latestPrice()).isEqualTo(requestDto.getStandardPrice()); // 초기 가격은 처음 기준 가격
        Assertions.assertThat(findProduct.ownerId()).isEqualTo(requestDto.getOwnerId());
        Assertions.assertThat(findProduct.customerId()).isEqualTo(requestDto.getOwnerId()); // 초기 가격의 주인은 소유자
    }

    @Test
    @DisplayName("상품들 중 제목으로 검색해서 조회한다.")
    void searchProductsByTitleTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto1 = createProductRequestDto(0L,"모빌 팔아요", "사용한 건 1년 되었어요 ", "FURNITURE", 2000, member.id());
        ProductRequestDto requestDto2 = createProductRequestDto(0L,"가구 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id());
        ProductRequestDto requestDto3 = createProductRequestDto(0L,"간식 팔아요", "유통기한 7일 남았네요 ", "FURNITURE", 1000, member.id());
        productServiceImpl.insertProduct(requestDto1, null);
        productServiceImpl.insertProduct(requestDto2, null);
        productServiceImpl.insertProduct(requestDto3, null);

        Map<String, String> params = new HashMap<>();
        params.put("title", "모빌");
        params.put("content", "");

        // when
        List<ProductResponseDto> products = productServiceImpl.searchProductsByKeyword("post_date", "desc", params);

        // then
        Assertions.assertThat(products.size()).isEqualTo(1);
        Assertions.assertThat(products.get(0).title()).isEqualTo("모빌 팔아요");
        Assertions.assertThat(products.get(0).content()).isEqualTo("사용한 건 1년 되었어요 ");
        Assertions.assertThat(products.get(0).standardPrice()).isEqualTo(2000);
    }

    @Test
    @DisplayName("상품들 중 내용으로 검색해서 조회한다.")
    void searchProductsByContentTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto1 = createProductRequestDto(0L,"모빌 팔아요", "사용한 건 1년 되었어요 ", "FURNIURE", 2000, member.id());
        ProductRequestDto requestDto2 = createProductRequestDto(0L,"가구 팔아요", "사용한 건 2년 되었어요 ", "DEVICE", 1000, member.id());
        ProductRequestDto requestDto3 = createProductRequestDto(0L,"간식 팔아요", "유통기한 7일 남았네요 ", "DEVICE", 1000, member.id());
        productServiceImpl.insertProduct(requestDto1, null);
        productServiceImpl.insertProduct(requestDto2, null);
        productServiceImpl.insertProduct(requestDto3, null);

        Map<String, String> params = new HashMap<>();
        params.put("title", "");
        params.put("content", "2년");

        // when
        List<ProductResponseDto> products = productServiceImpl.searchProductsByKeyword("post_date", "desc", params);

        // then
        Assertions.assertThat(products.size()).isEqualTo(1);
        Assertions.assertThat(products.get(0).title()).isEqualTo("가구 팔아요");
        Assertions.assertThat(products.get(0).content()).isEqualTo("사용한 건 2년 되었어요 ");
        Assertions.assertThat(products.get(0).category()).isEqualTo("DEVICE");
        Assertions.assertThat(products.get(0).standardPrice()).isEqualTo(1000);
    }

    @Test
    @DisplayName("카테고리별 상품을 조회한다.")
    void findProductsByCategoryTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto1 = createProductRequestDto(0L,"모빌 팔아요", "사용한 건 1년 되었어요 ", "FURNITURE",  2000, member.id());
        ProductRequestDto requestDto2 = createProductRequestDto(0L,"가구 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id());
        ProductRequestDto requestDto3 = createProductRequestDto(0L,"간식 팔아요", "유통기한 7일 남았네요 ", "DEVICE", 1000, member.id());
        productServiceImpl.insertProduct(requestDto1, null);
        productServiceImpl.insertProduct(requestDto2, null);
        productServiceImpl.insertProduct(requestDto3, null);

        // when
        List<ProductResponseDto> products = productServiceImpl.findProductsByCategory("FURNITURE");

        // then
        assertThat(products)
                .hasSize(2)
                .extracting(ProductResponseDto::title)
                .containsExactly("모빌 팔아요", "가구 팔아요");
    }

    private static ProductRequestDto createProductRequestDto(Long productId, String title, String content, String category, int price, Long memberId) {
        return ProductRequestDto.builder()
                .id(productId)
                .title(title)
                .content(content)
                .category(category)
                .standardPrice(price)
                .ownerId(memberId)
                .build();
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
}