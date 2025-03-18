package vn.dangthehao.hotel_booking_management.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        int code = ErrorCode.UNAUTHORIZED.getCode();
        String message = ErrorCode.UNAUTHORIZED.getMessage();
        String body = String.format("""
                {
                    "status": "Error",
                    "code": %d,
                    "message": "%s"
                }
                """, code, message);
        response.getWriter().write(body);
    }
}
