package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.ProductBidDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductBidHistoryMapper {
    int addBidHistory(ProductBidDto productBidDto);
    void deleteAll();
}
