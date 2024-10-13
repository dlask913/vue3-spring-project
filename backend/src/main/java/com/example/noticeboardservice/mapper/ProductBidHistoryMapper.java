package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.ProductBidDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductBidHistoryMapper {
    int addBidHistory(ProductBidDto productBidDto);
    void deleteAll();
    List<ProductBidDto> findAllBidHistories();
}
