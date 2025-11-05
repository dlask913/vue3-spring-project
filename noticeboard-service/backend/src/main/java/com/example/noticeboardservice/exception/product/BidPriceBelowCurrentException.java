package com.example.noticeboardservice.exception.product;

import com.example.noticeboardservice.exception.common.BizException;

public class BidPriceBelowCurrentException extends BizException {
    @Override
    public int getStatus() {
        return 400;
    }

    @Override
    public String getMessage() {
        return "현재 가격보다 높은 가격을 입력해주세요.";
    }
}
