package com.toucheese.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toucheese.global.data.CommonResponse;
import com.toucheese.global.exception.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenUtils tokenUtils;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String[] excludePath = {
                "/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v1/auth/**",
                "/v3/api-docs/**", "/v1/admin/**", "/v1/studios/**", "/v1/products/**", "/v1/concepts/**", "/v1/reviews/**", "/v1/tokens/**",  "/v2/**", "/v1/images/**",
                "/v1/favorites/**"
        };
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = tokenUtils.getTokenFromAuthorizationHeader(request);
        try {
            if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken, false)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (GlobalCustomException e) {
            sendErrorResponse(response, e.getErrorCode());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("X-Error-Code", String.valueOf(errorCode.getCode()));

        CommonResponse<Object> errorResponse = CommonResponse.fail(errorCode);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(new ObjectMapper().writeValueAsString(errorResponse));
            writer.flush();
        }
    }
}
