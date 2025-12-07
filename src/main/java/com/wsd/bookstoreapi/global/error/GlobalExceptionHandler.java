package com.wsd.bookstoreapi.global.error;

import com.wsd.bookstoreapi.global.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 (도메인에서 직접 던지는 예외)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<Void>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode();

        log.warn(
                "BusinessException: code={}, message={}, path={}",
                errorCode.getCode(), ex.getMessage(), request.getRequestURI()
        );

        ApiResult<Void> body = ApiResult.error(errorCode, ex.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(body);
    }

    /**
     * Bean Validation (@Valid) 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn("Validation error, path={}", request.getRequestURI(), ex);

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResult<Map<String, String>> body = ApiResult.error(
                ErrorCode.VALIDATION_FAILED,
                "요청 값이 올바르지 않습니다.",
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * JSON 파싱 실패 등
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn("HttpMessageNotReadable, path={}", request.getRequestURI(), ex);

        ApiResult<Void> body = ApiResult.error(
                ErrorCode.INVALID_INPUT_VALUE,
                "요청 본문을 읽을 수 없습니다."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * 잘못된 path variable / request param 타입
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResult<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        log.warn("TypeMismatch, path={}", request.getRequestURI(), ex);

        ApiResult<Void> body = ApiResult.error(
                ErrorCode.INVALID_INPUT_VALUE,
                "요청 파라미터 타입이 올바르지 않습니다."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * 지원하지 않는 HTTP 메서드
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        log.warn("MethodNotSupported, path={}", request.getRequestURI(), ex);

        ApiResult<Void> body = ApiResult.error(
                ErrorCode.METHOD_NOT_ALLOWED,
                "지원하지 않는 HTTP 메서드입니다."
        );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(body);
    }

    /**
     * 마지막 방어선: 알 수 없는 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unknown error. path={}", request.getRequestURI(), ex);

        ApiResult<Void> body = ApiResult.error(
                ErrorCode.UNKNOWN_ERROR,
                "알 수 없는 오류가 발생했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}
