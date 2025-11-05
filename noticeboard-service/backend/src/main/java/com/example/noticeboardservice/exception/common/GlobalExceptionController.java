package com.example.noticeboardservice.exception.common;

import com.example.noticeboardservice.dto.common.ExceptionDto;
import com.example.noticeboardservice.exception.member.MemberDuplicateException;
import com.example.noticeboardservice.exception.member.MemberNotFoundException;
import com.example.noticeboardservice.exception.member.PasswordMismatchException;
import com.example.noticeboardservice.exception.product.BidPriceBelowCurrentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ExceptionDto> handlePasswordMismatchException(PasswordMismatchException e) {
        final ExceptionDto responseError = ExceptionDto.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(responseError.getStatus()).body(responseError);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleMemberNotFoundException(MemberNotFoundException e) {
        final ExceptionDto responseError = ExceptionDto.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(responseError.getStatus()).body(responseError);
    }

    @ExceptionHandler(BidPriceBelowCurrentException.class)
    public ResponseEntity<ExceptionDto> handleBidPriceBelowCurrentException(BidPriceBelowCurrentException e) {
        final ExceptionDto responseError = ExceptionDto.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(responseError.getStatus()).body(responseError);
    }

    @ExceptionHandler(MemberDuplicateException.class)
    public ResponseEntity<ExceptionDto> handleMemberDuplicateException(MemberDuplicateException e) {
        final ExceptionDto responseError = ExceptionDto.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(responseError.getStatus()).body(responseError);
    }
}
