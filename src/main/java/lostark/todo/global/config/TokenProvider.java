package lostark.todo.global.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.security.ApplicationOAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT(JSON Web Token) 생성과 유효성 검사를 담당하는 서비스 클래스
 * Updated to use JJWT 0.12.6 API with JDK17 compatibility
 */
@Service
@Slf4j
public class TokenProvider {

    @Value("${JWT-KEY}")
    private String secret;

    /**
     * Creates a signing key from the base64-encoded secret.
     * In JJWT 0.12.6, the signature algorithm is automatically determined based on the key size,
     * replacing the previous approach of explicitly specifying SignatureAlgorithm.HS512.
     */
    private SecretKey getSigningKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 일반 로그인 JWT 생성
     * Creates a JWT token for a standard login.
     * The signature algorithm is inferred from the secret key.
     */
    public String createToken(Member member) {
        SecretKey secretKey = getSigningKey(secret);
        return Jwts.builder()
                .subject(member.getUsername())
                .issuer("LostarkTodo")
                .issuedAt(new Date())
                .signWith(secretKey) // Algorithm is inferred from the key
                .compact();
    }

    /**
     * 구글 로그인 연동 Token 생성
     * Creates a JWT token for Google OAuth login.
     * The signature algorithm is inferred from the secret key.
     */
    public String createToken(Authentication authentication, String key) {
        ApplicationOAuth2User userPrincipal = (ApplicationOAuth2User) authentication.getPrincipal();
        SecretKey secretKey = getSigningKey(key);
        return Jwts.builder()
                .subject(userPrincipal.getName())
                .issuer("LostarkTodo")
                .issuedAt(new Date())
                .signWith(secretKey) // Algorithm is inferred from the key
                .compact();
    }

    /**
     * JWT 검증
     * Validates a JWT token and extracts the subject.
     * Uses the updated JJWT 0.12.6 parser API.
     */
    public String validToken(String token) {
        SecretKey secretKey = getSigningKey(secret);
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}