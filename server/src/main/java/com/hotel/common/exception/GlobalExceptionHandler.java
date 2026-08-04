package com.hotel.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerException(Exception e){
        log.error("server error : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        500,
                        "COMMON",
                        "INTERNAL_SERVER_ERROR",
                        "서버 오류가 발생했습니다"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .getFirst()
                .getDefaultMessage();
        log.error("validation error : {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        400,
                        "VALIDATION",
                        ErrorCode.INVALID_INPUT.name(),
                        message
                ));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handlerCustomException(CustomException e){
        ErrorCode errorCode = e.getErrorCode();

        if(errorCode.getStatus().is5xxServerError()){
            log.error("[{}] {}", errorCode.name(), e.getMessage());
        }
        else{
            log.warn("[{}] {}", errorCode.name(), e.getMessage());
        }

        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(
                       errorCode.getStatus().value(),
                        "BUSINESS",
                        errorCode.name(),
                        errorCode.getMessage()
                ));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockingFailureException e){
        log.warn("lock conflict : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        409,
                        "BUSINESS",
                        ErrorCode.OPTIMISTIC_LOCK_CONFLICT.name(),
                        ErrorCode.OPTIMISTIC_LOCK_CONFLICT.getMessage()
                ));
    }
}
