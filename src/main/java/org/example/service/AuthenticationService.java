package org.example.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.example.domain.User;
import org.example.exception.IncorrectPasswordException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;

    @Value("${signing.key:12345}")
    private String signingKey;

    private JwtBuilder tokensBuilder;

    @PostConstruct
    private void init() {
        this.tokensBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS384, signingKey);
    }

    @SneakyThrows
    public String authenticate(String username, String password) {
        User user = userService.findByUsername(username);

        log.info("Loaded user : {} by username : {}", user, username);

        // Assume we allow only ASCII symbols in passwords, which is true in 99.99% cases in real world
        String digest = Hashing.md5().hashString(password, StandardCharsets.US_ASCII).toString();

        if (Objects.equals(digest, user.getPassword())) {
            log.info("Provided password matched for user : {}", user);
            return tokensBuilder.claim("USER_ID", user.getId()).setIssuedAt(new Date()).compact();
        } else {
            log.warn("Provided password for user : {} did not match the password in DB", user);
            throw new IncorrectPasswordException("Provided password does not match the password in DB");
        }
    }
}