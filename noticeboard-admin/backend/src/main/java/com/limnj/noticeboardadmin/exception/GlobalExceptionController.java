package com.limnj.noticeboardadmin.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ExceptionDto> handleBizException(BizException e) {
        ExceptionDto responseError = ExceptionDto.builder()
                .status(e.getErrorCode().getStatus())
                .errorCode(e.getErrorCode().name())
                .message(e.getErrorCode().getDescription())
                .build();
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(responseError);
    }
}
