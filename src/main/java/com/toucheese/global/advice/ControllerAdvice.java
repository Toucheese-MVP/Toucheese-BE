package com.toucheese.global.advice;

import com.toucheese.global.exception.ToucheeseException;
import com.toucheese.global.exception.ToucheeseJwtException;
import com.toucheese.global.data.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ToucheeseException.class)
    public ResponseEntity<String> handleException(ToucheeseException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    // ToucheeseJwtException을 처리하는 새로운 예외 처리 로직
    @ResponseBody
    @ExceptionHandler(ToucheeseJwtException.class)
    public ResponseEntity<ErrorResponse<?>> handleJwtException(ToucheeseJwtException e) {
        log.error("JWT 에러 발생: {}", e.getMessage());
        ErrorResponse<?> errorResponse = ErrorResponse.fail(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
    }


}
