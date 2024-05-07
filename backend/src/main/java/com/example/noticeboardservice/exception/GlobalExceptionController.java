package com.example.noticeboardservice.exception;

import com.example.noticeboardservice.dto.ExceptionDto;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ExceptionDto> handlePasswordMismatchException() {
        final ExceptionDto responseError = ExceptionDto.builder()
                .status(400)
                .message("비밀번호를 확인해주세요.")
                .build();
        return ResponseEntity.status(responseError.getStatus()).body(responseError);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleMemberNotFoundException() {
        final ExceptionDto responseError = ExceptionDto.builder()
                .status(400)
                .message("회원 정보가 존재하지 않습니다.")
                .build();
        return ResponseEntity.status(responseError.getStatus()).body(responseError);
    }

}
