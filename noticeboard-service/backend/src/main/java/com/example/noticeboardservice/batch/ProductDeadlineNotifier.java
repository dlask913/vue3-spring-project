package com.example.noticeboardservice.batch;

import com.example.noticeboardservice.dto.product.ProductBidDto;
import com.example.noticeboardservice.dto.product.ProductResponseDto;
import com.example.noticeboardservice.service.product.ProductBidService;
import com.example.noticeboardservice.service.product.ProductService;
import com.limnj.mail_sender.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductDeadlineNotifier {
    private final ProductService productServiceImpl;
    private final ProductBidService productBidServiceImpl;
    private final MailService mailService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00 실행
    public void notifyBidResult() {
        List<ProductResponseDto> products = productServiceImpl.findProductsByDeadline();
        for (ProductResponseDto product : products) {
            ProductBidDto bidResult = productBidServiceImpl.findLatestBidHistory(product.id());
            mailService.sendMail(bidResult.getCustomerEmail(),
                    "상품 입찰에 성공하였습니다.", product.title() + "입찰에 성공하였습니다.");
        }
    }
}
