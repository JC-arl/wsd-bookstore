package com.wsd.bookstoreapi.global.health;

import com.wsd.bookstoreapi.global.api.ApiResult;
import com.wsd.bookstoreapi.global.health.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Health", description = "헬스체크 API")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @Operation(
            summary = "헬스체크",
            description = "인증 없이 호출 가능. 애플리케이션/DB/Redis 상태를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서비스 정상 동작 중")
    })
    @GetMapping("/health")
    public ResponseEntity<ApiResult<HealthResponse>> health() {
        HealthResponse health = healthService.getHealth();
        ApiResult<HealthResponse> apiResult = ApiResult.success(
                health,
                "OK"
        );
        return ResponseEntity.ok(apiResult);
    }
}
