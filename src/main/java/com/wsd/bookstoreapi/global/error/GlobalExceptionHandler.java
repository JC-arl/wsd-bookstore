package com.wsd.bookstoreapi.global.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 비즈니스 예외 (도메인 에러)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode();
        String path = request.getRequestURI();

        log.warn("BusinessException: code={}, message={}, path={}",
                errorCode.getCode(), ex.getMessage(), path);

        ErrorResponse body = ErrorResponse.of(
                path,
                errorCode,
                ex.getMessage(),
                ex.getDetails()
        );
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(body);
    }

    /**
     * @Valid, @Validated 바인딩 에러 (RequestBody DTO 검증 실패)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();

        Map<String, Object> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        log.warn("Validation failed: path={}, errors={}", path, details);

        ErrorResponse body = ErrorResponse.of(
                path,
                ErrorCode.VALIDATION_FAILED,
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                details
        );

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(body);
    }

    /**
     * @Validated + PathVariable/RequestParam 등에 대한 검증 실패
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();

        Map<String, Object> details = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            details.put(field, violation.getMessage());
        });

        log.warn("Constraint violation: path={}, errors={}", path, details);

        ErrorResponse body = ErrorResponse.of(
                path,
                ErrorCode.VALIDATION_FAILED,
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                details
        );

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(body);
    }

    /**
     * HTTP 메서드가 지원되지 않을 때 (예: POST만 지원하는데 GET 호출)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();

        log.warn("Method not supported: method={}, path={}", ex.getMethod(), path);

        Map<String, Object> details = new HashMap<>();
        details.put("method", ex.getMethod());

        ErrorResponse body = ErrorResponse.of(
                path,
                ErrorCode.BAD_REQUEST,
                "지원하지 않는 HTTP 메서드입니다.",
                details
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * 지원하지 않는 Content-Type 등
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();

        log.warn("Media type not supported: contentType={}, path={}",
                ex.getContentType(), path);

        Map<String, Object> details = new HashMap<>();
        if (ex.getContentType() != null) {
            details.put("contentType", ex.getContentType().toString());
        }

        ErrorResponse body = ErrorResponse.of(
                path,
                ErrorCode.BAD_REQUEST,
                "지원하지 않는 Content-Type 입니다.",
                details
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * Spring Security AccessDenied (권한 부족, 403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();

        log.warn("Access denied: path={}, message={}", path, ex.getMessage());

        ErrorResponse body = ErrorResponse.of(
                path,
                ErrorCode.FORBIDDEN
        );

        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getHttpStatus())
                .body(body);
    }

    /**
     * 최종 fallback - 여기까지 오면 알 수 없는 에러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        // 스택 트레이스는 로그로 남기고
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        // 디버깅 편하게: 실제 예외 메시지를 details에 넣어줌
        Map<String, Object> details = new HashMap<>();
        details.put("exception", ex.getClass().getName());
        details.put("message", ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                request.getRequestURI(),
                ErrorCode.UNKNOWN_ERROR,
                details
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
