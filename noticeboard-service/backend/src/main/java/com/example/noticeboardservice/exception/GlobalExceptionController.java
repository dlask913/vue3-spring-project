package com.example.noticeboardservice.exception;

import com.example.noticeboardservice.dto.ExceptionDto;
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

}
