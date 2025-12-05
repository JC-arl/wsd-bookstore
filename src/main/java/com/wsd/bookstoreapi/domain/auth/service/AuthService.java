package com.wsd.bookstoreapi.domain.auth.service;

import com.wsd.bookstoreapi.domain.auth.dto.AuthResponse;
import com.wsd.bookstoreapi.domain.auth.dto.LoginRequest;
import com.wsd.bookstoreapi.domain.auth.dto.RefreshTokenRequest;
import com.wsd.bookstoreapi.domain.auth.dto.SignUpRequest;
import com.wsd.bookstoreapi.domain.user.entity.AuthProvider;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.entity.UserRole;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import com.wsd.bookstoreapi.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wsd.bookstoreapi.domain.auth.service.RedisAuthTokenService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisAuthTokenService redisAuthTokenService;

    /**
     * 회원가입 (LOCAL)
     */
    @Transactional
    public void signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "이미 사용 중인 이메일입니다."
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))    //비밀번호 해싱(bcrypt)
                .role(UserRole.ROLE_USER)
                .provider(AuthProvider.LOCAL)
                .providerId(null)
                .status("ACTIVE")
                .build();

        userRepository.save(user);
    }

    /**
     * 로그인 (이메일 + 비밀번호)
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "이메일 또는 비밀번호가 올바르지 않습니다."
                ));

        // provider가 LOCAL인지 확인 (추후 OAuth2 계정과 구분)
        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED,
                    "소셜 로그인 계정입니다. 소셜 로그인을 사용해 주세요."
            );
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED,
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        String role = user.getRole().name();
        String email = user.getEmail();
        Long userId = user.getId();

        String accessToken = jwtTokenProvider.generateAccessToken(userId, email, role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, email, role);

        // Redis에 Refresh Token 저장
        redisAuthTokenService.saveRefreshToken(
                userId,
                refreshToken,
                jwtTokenProvider.getRefreshTokenValidityInMs()  // @Getter 사용
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenValidityInMs())
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenValidityInMs())
                .tokenType("Bearer")
                .build();
    }

    /**
     * Refresh Token으로 Access Token 재발급
     * (여기서는 refreshToken도 JWT로 보고, 서명/만료만 검증하는 방식)
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 유효성 검증 (만료/서명 오류시 BusinessException 발생)
        jwtTokenProvider.validateToken(refreshToken);

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String email = jwtTokenProvider.getEmail(refreshToken);
        String role = jwtTokenProvider.getRole(refreshToken);

        // 유저가 아직 존재하는지 확인 (삭제/비활성 사용자 방지용)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "비활성화된 계정입니다."
            );
        }

        // 3) Redis에 저장된 Refresh Token과 일치하는지 확인
        String storedRefreshToken = redisAuthTokenService.getRefreshToken(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.UNAUTHORIZED,
                        "로그인이 필요합니다."
                ));

        if (!storedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED,
                    "유효하지 않은 Refresh Token 입니다."
            );
        }

        // 4) 새 토큰 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, email, role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, email, role);

        // 5) Redis에 새 Refresh Token으로 교체
        redisAuthTokenService.saveRefreshToken(
                userId,
                newRefreshToken,
                jwtTokenProvider.getRefreshTokenValidityInMs()
        );

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenValidityInMs())
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenValidityInMs())
                .tokenType("Bearer")
                .build();
    }
}
