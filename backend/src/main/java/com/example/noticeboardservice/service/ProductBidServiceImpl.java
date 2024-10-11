package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ProductBidDto;
import com.example.noticeboardservice.mapper.ProductBidHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
@RequiredArgsConstructor
public class ProductBidServiceImpl implements ProductBidService{
    private final ProductBidHistoryMapper productBidHistoryMapper;

    @Override
    public int addBidHistory(ProductBidDto productBidDto) {
        return productBidHistoryMapper.addBidHistory(productBidDto);
    }
}
