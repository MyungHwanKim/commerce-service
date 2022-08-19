package com.zerobase.user.controller;

import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({
            CustomException.class
    })
    public ResponseEntity<ExceptionResponse> customRequestException(final CustomException customException) {
        log.warn("api Exception : {}", customException.getErrorCode());
        return ResponseEntity.badRequest().body(new ExceptionResponse(customException.getMessage(), customException.getErrorCode()));
    }


    @Getter
    @ToString
    @AllArgsConstructor
    public static class ExceptionResponse {
        private String message;
        private ErrorCode errorCode;

    }
}