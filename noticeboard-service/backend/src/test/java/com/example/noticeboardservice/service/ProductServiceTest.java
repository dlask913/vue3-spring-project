package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.ProductBidHistoryMapper;
import com.example.noticeboardservice.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductService productServiceImpl;

    @Autowired
    ProductBidHistoryMapper productBidHistoryMapper;
    @Autowired
    MemberMapper memberMapper;

    @Test
    @DisplayName("판매할 상품을 이미지 없이 등록한다.")
    void insertProductWithoutImageTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id(), "2025-03-25");

        // when
        productServiceImpl.insertProduct(requestDto, null);

        // then
        ProductDetailsResponseDto findProduct = productMapper.findProduct(requestDto.getId());

        assertThat(findProduct.title()).isEqualTo(requestDto.getTitle());
        assertThat(findProduct.content()).isEqualTo(requestDto.getContent());
        assertThat(findProduct.standardPrice()).isEqualTo(requestDto.getStandardPrice());
        assertThat(findProduct.ownerId()).isEqualTo(requestDto.getOwnerId());
        assertThat(findProduct.imgUrl()).isEqualTo("/image/productDefaultImg.jpg");
        assertThat(findProduct.deadline()).isEqualTo(requestDto.getDeadline());
    }

    @Test
    @DisplayName("상품을 등록하면 초기 가격 히스토리가 등록된다.")
    void checkPriceWhenProductInsertTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id(),"2025-03-25");

        // when
        productServiceImpl.insertProduct(requestDto, null);

        // then
        ProductBidDto latestBidHistory = productBidHistoryMapper.findLatestBidHistory(requestDto.getId());
        assertThat(latestBidHistory.getProductId()).isEqualTo(requestDto.getId());
        assertThat(latestBidHistory.getBidPrice()).isEqualTo(requestDto.getStandardPrice());
        assertThat(latestBidHistory.getCustomerId()).isEqualTo(requestDto.getOwnerId());
    }


    @Test
    @DisplayName("판매할 상품을 이미지와 함께 등록한다.")
    void insertProductWithImageTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id(),"2025-03-25");
        MockMultipartFile productImg = new MockMultipartFile(
                "상품 이미지",
                "productImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "productImg!".getBytes()
        );

        // when
        productServiceImpl.insertProduct(requestDto, productImg);

        // then
        ProductDetailsResponseDto findProduct = productMapper.findProduct(requestDto.getId());
        assertThat(findProduct.title()).isEqualTo(requestDto.getTitle());
        assertThat(findProduct.content()).isEqualTo(requestDto.getContent());
        assertThat(findProduct.standardPrice()).isEqualTo(requestDto.getStandardPrice());
        assertThat(findProduct.ownerId()).isEqualTo(requestDto.getOwnerId());
        assertThat(findProduct.imgUrl()).startsWith("/images/product/");
        assertThat(findProduct.deadline()).isEqualTo(requestDto.getDeadline());
    }

    @Test
    @DisplayName("판매할 상품의 제목, 내용, 가격을 수정한다.")
    void updateProductTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id(),"2025-03-25");
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findProduct(requestDto.getId()).id();
        ProductRequestDto updateDto = createProductRequestDto(
                productId, "고양이 장난감 팔아요", "강아지 장난감은 다 팔렸어요", "FURNITURE", 220, requestDto.getOwnerId(),"2025-03-25");

        // when
        productServiceImpl.updateProduct(updateDto);

        // then
        ProductDetailsResponseDto findProduct = productMapper.findProduct(requestDto.getId());
        assertThat(findProduct.title()).isEqualTo(updateDto.getTitle());
        assertThat(findProduct.content()).isEqualTo(updateDto.getContent());
        assertThat(findProduct.standardPrice()).isEqualTo(updateDto.getStandardPrice());
        assertThat(findProduct.ownerId()).isEqualTo(updateDto.getOwnerId());
        assertThat(findProduct.deadline()).isEqualTo(requestDto.getDeadline());
    }

    @Test
    @DisplayName("등록한 상품을 삭제한다.")
    void deleteProductTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id(),"2025-03-25");
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findProduct(requestDto.getId()).id();

        // when
        // todo: Cannot delete or update a parent row: a foreign key constraint fails (`testboard`.`bid_history`, CONSTRAINT `bid_history_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`))
        productServiceImpl.deleteProduct(productId);

        // then
        assertThat(productMapper.findAllProducts().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 상품을 단일 조회한다.")
    void findProductTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto = createProductRequestDto(0L,"강아지 장난감 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id(),"2025-03-25");
        productServiceImpl.insertProduct(requestDto, null);

        Long productId = productMapper.findProduct(requestDto.getId()).id();

        // when
        ProductDetailsResponseDto findProduct = productServiceImpl.findProduct(productId);

        // then
        assertThat(findProduct.title()).isEqualTo(requestDto.getTitle());
        assertThat(findProduct.content()).isEqualTo(requestDto.getContent());
        assertThat(findProduct.standardPrice()).isEqualTo(requestDto.getStandardPrice());
        assertThat(findProduct.latestPrice()).isEqualTo(requestDto.getStandardPrice()); // 초기 가격은 처음 기준 가격
        assertThat(findProduct.ownerId()).isEqualTo(requestDto.getOwnerId());
        assertThat(findProduct.customerId()).isEqualTo(requestDto.getOwnerId()); // 초기 가격의 주인은 소유자
        assertThat(findProduct.deadline()).isEqualTo(requestDto.getDeadline());
    }

    @Test
    @DisplayName("상품들 중 제목으로 검색해서 조회한다.")
    void searchProductsByTitleTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto1 = createProductRequestDto(0L,"모빌 팔아요", "사용한 건 1년 되었어요 ", "FURNITURE", 2000, member.id(),"2025-03-25");
        ProductRequestDto requestDto2 = createProductRequestDto(0L,"가구 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id(),"2025-03-25");
        ProductRequestDto requestDto3 = createProductRequestDto(0L,"간식 팔아요", "유통기한 7일 남았네요 ", "FURNITURE", 1000, member.id(),"2025-03-25");
        productServiceImpl.insertProduct(requestDto1, null);
        productServiceImpl.insertProduct(requestDto2, null);
        productServiceImpl.insertProduct(requestDto3, null);

        Map<String, String> params = new HashMap<>();
        params.put("title", "모빌");
        params.put("content", "");

        // when
        List<ProductResponseDto> products = productServiceImpl.searchProductsByKeyword("post_date", "desc", params);

        // then
        assertThat(products.size()).isEqualTo(1);
        assertThat(products.get(0).title()).isEqualTo("모빌 팔아요");
        assertThat(products.get(0).content()).isEqualTo("사용한 건 1년 되었어요 ");
        assertThat(products.get(0).standardPrice()).isEqualTo(2000);
        assertThat(products.get(0).postType()).isEqualTo(PostType.PRODUCT);
    }

    @Test
    @DisplayName("상품들 중 내용으로 검색해서 조회한다.")
    void searchProductsByContentTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto1 = createProductRequestDto(0L,"모빌 팔아요", "사용한 건 1년 되었어요 ", "FURNIURE", 2000, member.id(),"2025-03-25");
        ProductRequestDto requestDto2 = createProductRequestDto(0L,"가구 팔아요", "사용한 건 2년 되었어요 ", "DEVICE", 1000, member.id(),"2025-03-25");
        ProductRequestDto requestDto3 = createProductRequestDto(0L,"간식 팔아요", "유통기한 7일 남았네요 ", "DEVICE", 1000, member.id(),"2025-03-25");
        productServiceImpl.insertProduct(requestDto1, null);
        productServiceImpl.insertProduct(requestDto2, null);
        productServiceImpl.insertProduct(requestDto3, null);

        Map<String, String> params = new HashMap<>();
        params.put("title", "");
        params.put("content", "2년");

        // when
        List<ProductResponseDto> products = productServiceImpl.searchProductsByKeyword("post_date", "desc", params);

        // then
        assertThat(products.size()).isEqualTo(1);
        assertThat(products.get(0).title()).isEqualTo("가구 팔아요");
        assertThat(products.get(0).content()).isEqualTo("사용한 건 2년 되었어요 ");
        assertThat(products.get(0).category()).isEqualTo("DEVICE");
        assertThat(products.get(0).standardPrice()).isEqualTo(1000);
        assertThat(products.get(0).postType()).isEqualTo(PostType.PRODUCT);
    }

    @Test
    @DisplayName("카테고리별 상품을 조회한다.")
    void findProductsByCategoryTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        ProductRequestDto requestDto1 = createProductRequestDto(0L,"모빌 팔아요", "사용한 건 1년 되었어요 ", "FURNITURE",  2000, member.id(),"2025-03-25");
        ProductRequestDto requestDto2 = createProductRequestDto(0L,"가구 팔아요", "사용한 건 2년 되었어요 ", "FURNITURE", 1000, member.id(),"2025-03-25");
        ProductRequestDto requestDto3 = createProductRequestDto(0L,"간식 팔아요", "유통기한 7일 남았네요 ", "DEVICE", 1000, member.id(),"2025-03-25");
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

    @Test
    @DisplayName("회원이 등록한 상품 게시물들을 모두 조회한다.")
    void findProductsByMemberIdTest(){
        // given
        MemberResponseDto member1 = getMember("limnj1@test.com");
        MemberResponseDto member2 = getMember("limnj2@test.com");
        ProductRequestDto requestDto1 = createProductRequestDto(0L,"모빌 팔아요", "사용한 건 1년 되었어요 ", "FURNIURE", 2000, member1.id(),"2025-03-25");
        ProductRequestDto requestDto2 = createProductRequestDto(0L,"가구 팔아요", "사용한 건 2년 되었어요 ", "DEVICE", 1000, member2.id(),"2025-03-25");
        ProductRequestDto requestDto3 = createProductRequestDto(0L,"간식 팔아요", "유통기한 7일 남았네요 ", "DEVICE", 1000, member1.id(),"2025-03-25");
        ProductRequestDto requestDto4 = createProductRequestDto(0L,"TV 팔아요", "유통기한 7일 남았네요 ", "DEVICE", 1000, member2.id(),"2025-03-25");
        ProductRequestDto requestDto5 = createProductRequestDto(0L,"노트북 팔아요", "유통기한 7일 남았네요 ", "DEVICE", 1000, member2.id(),"2025-03-25");
        productServiceImpl.insertProduct(requestDto1, null);
        productServiceImpl.insertProduct(requestDto2, null);
        productServiceImpl.insertProduct(requestDto3, null);
        productServiceImpl.insertProduct(requestDto4, null);
        productServiceImpl.insertProduct(requestDto5, null);

        // when
        List<ProductResponseDto> products = productServiceImpl.findProductsByMemberId(member1.id());

        // then
        assertThat(products)
                .hasSize(2)
                .extracting(ProductResponseDto::title)
                .containsExactly("모빌 팔아요", "간식 팔아요");
    }

    @Test
    @DisplayName("마감일이 오늘인 상품들을 모두 조회한다.")
    void findProductsByDeadline(){
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        String today = java.time.LocalDate.now().toString();
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();

        ProductRequestDto product1 = createProductRequestDto(0L, "오늘 마감 상품 1", "내용", "DEVICE", 1000, member.id(), today);
        ProductRequestDto product2 = createProductRequestDto(0L, "오늘 마감 상품 2", "내용", "DEVICE", 2000, member.id(), today);
        ProductRequestDto product3 = createProductRequestDto(0L, "내일 마감 상품", "내용", "DEVICE", 3000, member.id(), futureDate);

        productServiceImpl.insertProduct(product1, null);
        productServiceImpl.insertProduct(product2, null);
        productServiceImpl.insertProduct(product3, null);

        // when
        List<ProductResponseDto> products = productServiceImpl.findProductsByDeadline();

        // then
        assertThat(products)
                .hasSize(2)
                .extracting(ProductResponseDto::title)
                .containsExactlyInAnyOrder("오늘 마감 상품 1", "오늘 마감 상품 2");
    }

    private static ProductRequestDto createProductRequestDto(Long productId, String title, String content, String category, int price, Long memberId, String deadline) {
        return ProductRequestDto.builder()
                .id(productId)
                .title(title)
                .content(content)
                .category(category)
                .standardPrice(price)
                .ownerId(memberId)
                .deadline(deadline)
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

