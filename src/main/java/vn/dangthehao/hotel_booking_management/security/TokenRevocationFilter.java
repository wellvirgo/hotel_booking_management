package vn.dangthehao.hotel_booking_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.service.TokenBlackListService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class TokenRevocationFilter extends OncePerRequestFilter {
    TokenBlackListService tokenBlackListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> tokenOptional = extractToken(request);
        if (tokenOptional.isPresent() && tokenBlackListService.isRevoked(tokenOptional.get())) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorCode errorCode = ErrorCode.TOKEN_IS_REVOKED;
            int code = errorCode.getCode();
            String message = errorCode.getMessage();
            String body = String.format("""
                    {
                        "status": "Error",
                        "code": %d,
                        "message": "%s"
                    }
                    """, code, message);
            response.getWriter().write(body);
            response.getWriter().flush();
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7));
        }
        return Optional.empty();
    }
}
