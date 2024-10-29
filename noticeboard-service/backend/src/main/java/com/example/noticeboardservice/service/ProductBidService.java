package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ProductBidDto;
import org.springframework.stereotype.Service;

@Service
public interface ProductBidService {
    int addBidHistory(ProductBidDto productBidDto);
}
