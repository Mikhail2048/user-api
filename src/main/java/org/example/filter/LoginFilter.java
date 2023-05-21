package org.example.filter;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.example.exception.ClientSideException;
import org.example.service.AuthenticationService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;
    private final String BASIC_AUTH_PREFIX = "Basic ";

    @Override
    protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
    ) {
        String basicAuthHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(basicAuthHeader)) {
            log.warn("Absent Authorization header in request");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (basicAuthHeader.startsWith(BASIC_AUTH_PREFIX)) {
            String token = basicAuthHeader.substring(BASIC_AUTH_PREFIX.length());
            String[] usernamePassword = new String(Base64.getDecoder().decode(token)).split(":");
            if (usernamePassword.length != 2) {
                throw new ClientSideException("Provided Basic auth token is malformed");
            }
            String jwtToken = authenticationService.authenticate(usernamePassword[0], usernamePassword[1]);
            response.setHeader("X-Auth-Token", jwtToken);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            log.warn("Basic auth token is not prefixed with 'Basic'");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getRequestURI().equals("/login");
    }
}
