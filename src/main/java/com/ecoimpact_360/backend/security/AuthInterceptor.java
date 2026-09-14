package com.ecoimpact_360.backend.security;
import com.ecoimpact_360.backend.dto.ApiErrorDTO;
import com.ecoimpact_360.backend.exception.ErrorCode;
import com.ecoimpact_360.backend.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.Instant;

public class AuthInterceptor implements HandlerInterceptor {
    public static final String SCHOOL_ID_ATTRIBUTE = "schoolId";

    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        try {
            if (header == null || !header.startsWith("Bearer ")) {
                throw new UnauthorizedException("Falta el token de autenticación");
            }
            Long schoolId = tokenService.validateAndGetSchoolId(header.substring("Bearer ".length()));
            request.setAttribute(SCHOOL_ID_ATTRIBUTE, schoolId);
            return true;
        } catch (UnauthorizedException ex) {
            writeUnauthorized(response, request, ex.getMessage());
            return false;
        }
    }

    private void writeUnauthorized(HttpServletResponse response, HttpServletRequest request, String message) throws Exception {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorDTO error = new ApiErrorDTO(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                message,
                request.getRequestURI(),
                ErrorCode.UNAUTHORIZED.getCode(),
                null
        );
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
