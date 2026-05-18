package com.hotel.payment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerException(Exception e){
        e.printStackTrace();
        log.error("server error : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        500,
                        "INTERNAL_SERVER_ERROR",
                        "서버 오류가 발생했습니다"
                ));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handlerCustomException(CustomException e){
        ErrorCode errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(
                       errorCode.getStatus().value(),
                        errorCode.name(),
                        errorCode.getMessage()
                ));
    }
}
