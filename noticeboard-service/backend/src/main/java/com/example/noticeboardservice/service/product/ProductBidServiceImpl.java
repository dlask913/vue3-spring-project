package com.example.noticeboardservice.service.product;

import com.example.noticeboardservice.dto.product.ProductBidDto;
import com.example.noticeboardservice.exception.product.BidPriceBelowCurrentException;
import com.example.noticeboardservice.mapper.product.ProductBidHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
@RequiredArgsConstructor
public class ProductBidServiceImpl implements ProductBidService{
    private final ProductBidHistoryMapper productBidHistoryMapper;

    @Override
    public int addBidHistory(ProductBidDto productBidDto) {
        if(isPriceLowerCurrentStandard(productBidDto.getProductId(), productBidDto.getBidPrice())){
            throw new BidPriceBelowCurrentException();
        }
        return productBidHistoryMapper.addBidHistory(productBidDto);
    }

    @Override
    public ProductBidDto findLatestBidHistory(Long productId) {
        return productBidHistoryMapper.findLatestBidHistory(productId);
    }

    private boolean isPriceLowerCurrentStandard(Long productId, int inputPrice) {
        int latestBidPrice = productBidHistoryMapper.findLatestBidHistory(productId).getBidPrice();
        return inputPrice <= latestBidPrice;
    }
}
