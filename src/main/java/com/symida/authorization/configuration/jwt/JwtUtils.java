package com.symida.authorization.configuration.jwt;

import com.symida.authorization.model.Account;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${symida.auth.jwt.secret}")
    private String jwtSecret;

    @Value("${symida.auth.jwt.expirationMs}")
    private int jwtExpirationMs;

    private final String tokenPrefix = "Bearer ";


    public String getJwtFromHeader(HttpServletRequest request) {
        var header = "Authorization";
        var bearerToken = request.getHeader(header);

        if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }

    public String generateJwtHeader(Account account) {
        return tokenPrefix + generateTokenFromUsername(account.getUsername());
    }

    public String getUserNameFromJwtToken(String token) {
        return jwtParser()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            jwtParser().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String generateTokenFromUsername(String username) {
        var now = Instant.now();
        var expirationInstant = now.plusMillis(jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationInstant))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private JwtParser jwtParser() {
        return Jwts.parser()
                .verifyWith(key())
                .build();
    }
}