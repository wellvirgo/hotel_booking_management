package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.RefreshToken;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.RefreshTokenRepository;
import vn.dangthehao.hotel_booking_management.util.JwtUtil;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;
    JwtUtil jwtUtil;

    public RefreshToken create(String token, User user) throws ParseException {
        Map<String, Object> claims = jwtUtil.getClaims(token);
        String tokenID = jwtUtil.getTokenID(claims);
        LocalDateTime expiredTime = jwtUtil.getExpiredTime(claims);
        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenID)
                .token(token)
                .user(user)
                .expiredTime(expiredTime)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void updateRefreshToken(User user, String refreshToken) {
        try {
            Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUser(user);
            create(refreshToken, user);
            refreshTokenOptional.ifPresent(this::delete);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public RefreshToken findByUser(User user) {
        return refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    public boolean verifyRefreshToken(String refreshToken) {
        String tokenID = jwtUtil.getTokenID(jwtUtil.getClaims(refreshToken));
        return refreshTokenRepository.existsById(tokenID);
    }

    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
