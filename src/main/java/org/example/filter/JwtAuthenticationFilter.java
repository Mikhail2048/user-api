package org.example.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtParser parser;

    @Value("${signing.key:12345}")
    private String signingKey;

    @PostConstruct
    private void init() {
        this.parser = Jwts.parser().setSigningKey(
          signingKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authorizationHeader)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        this.continueFilteringWithToken(request, response, filterChain, authorizationHeader);
    }

    private void continueFilteringWithToken(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain,
        String authorizationHeader
    ) throws IOException, ServletException {

        try {
            Claims claims = parser.parseClaimsJws(authorizationHeader).getBody();

            if (!StringUtils.hasText(claims.get("USER_ID", String.class))) {
                Long userId = Long.parseLong(claims.getSubject());
                log.info("Set userId : '{}' for thread : {}", userId, Thread.currentThread().getName());
                SecurityContextHolder.setUserId(userId);
            }
        } catch (Exception e) {
            log.warn("Unexpected error occurred : {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().equals("/login");
    }
}
