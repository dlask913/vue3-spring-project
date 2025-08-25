package com.limnj.noticeboardadmin.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ExceptionDto> handleBizException(BizException e) {
        ExceptionDto responseError = ExceptionDto.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(e.getStatus()).body(responseError);
    }
}
