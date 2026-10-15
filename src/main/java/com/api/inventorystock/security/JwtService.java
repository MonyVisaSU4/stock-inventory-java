package com.api.inventorystock.security;

import com.api.inventorystock.common.configuration.AppProperty;
import com.api.inventorystock.common.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {
    private final AppProperty appProperty;
    private final SecretKey signingKey;

    public JwtService(AppProperty appProperty) {
        this.appProperty = appProperty;
        this.signingKey = Keys.hmacShaKeyFor(appProperty.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private String token(String subject) {
        Date now = new Date();

        return Jwts.builder()
                .claim("type", "access")
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + appProperty.getJwt().getAccessExpiration()))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    private String refresh(String subject) {
        Date now = new Date();

        return Jwts.builder()
                .claim("type", "refresh")
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + appProperty.getJwt().getRefreshExpiration()))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public TokenResponse generateAccess(UserDetails userDetails) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + appProperty.getJwt().getAccessExpiration());

        String token = this.token(userDetails.getUsername());
        String refresh = this.refresh(userDetails.getUsername());

        return TokenResponse.builder()
                .access(token)
                .refresh(refresh)
                .issuedAt(issuedAt)
                .expiredAt(expiredAt)
                .build();
    }

    public TokenResponse generateRefresh(UserDetails userDetails) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + appProperty.getJwt().getAccessExpiration());

        String token = this.token(userDetails.getUsername());

        return TokenResponse.builder()
                .access(token)
                .issuedAt(issuedAt)
                .expiredAt(expiredAt)
                .build();
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenValid(String token) {
        return extractExpirationDate(token).after(new Date());
    }

    public boolean validateAccessToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        return "access".equals(extractClaim(token, claims -> claims.get("type", String.class)))
                && username.equals(userDetails.getUsername())
                && isTokenValid(token);
    }

    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        return "refresh".equals(extractClaim(token, claims -> claims.get("type", String.class)))
                && username.equals(userDetails.getUsername())
                && isTokenValid(token);
    }
}
