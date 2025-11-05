package com.example.noticeboardservice.service.product;

import com.example.noticeboardservice.dto.product.ProductBidDto;
import org.springframework.stereotype.Service;

@Service
public interface ProductBidService {
    int addBidHistory(ProductBidDto productBidDto);
    ProductBidDto findLatestBidHistory(Long productId);
}
