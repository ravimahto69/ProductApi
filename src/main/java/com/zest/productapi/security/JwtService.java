package com.zest.productapi.security;

import com.zest.productapi.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final Set<String> activeRefreshTokens = ConcurrentHashMap.newKeySet();

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String createAccessToken(User user) {
        return createToken(user, accessExpiration, "access");
    }

    public String createRefreshToken(User user) {
        String token = createToken(user, refreshExpiration, "refresh");
        activeRefreshTokens.add(token);
        return token;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public boolean isValid(String token) {
        try {
            return extractAllClaims(token).getExpiration().after(new Date());
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public boolean isActiveRefreshToken(String token) {
        return activeRefreshTokens.contains(token);
    }

    public void rotateRefreshToken(String token) {
        activeRefreshTokens.remove(token);
    }

    private String createToken(User user, long expiration, String type) {
        Date now = new Date();
        return Jwts.builder()
                .claims(Map.of("role", user.getRole().name(), "type", type))
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(signingKey)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
