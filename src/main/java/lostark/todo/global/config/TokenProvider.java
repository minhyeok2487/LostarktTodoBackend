package lostark.todo.global.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.security.ApplicationOAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * JWT(JSON Web Token) 생성과 유효성 검사를 담당하는 서비스 클래스
 */
@Service
@Slf4j
public class TokenProvider {

    @Value("${JWT-KEY}")
    private String secret;

    private Key getSigningKey(String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    /**
     * 일반 로그인 JWT 생성
     */
    public String createToken(Member member) {
        Key secretKey = getSigningKey(secret);
        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS512) // 최신 방식
                .setSubject(member.getUsername())
                .setIssuer("LostarkTodo")
                .setIssuedAt(new Date())
                .compact();
    }

    /**
     * 구글 로그인 연동 Token 생성
     */
    public String createToken(Authentication authentication, String key) {
        ApplicationOAuth2User userPrincipal = (ApplicationOAuth2User) authentication.getPrincipal();
        Key secretKey = getSigningKey(key);
        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS512) // 최신 방식
                .setSubject(userPrincipal.getName())
                .setIssuer("LostarkTodo")
                .setIssuedAt(new Date())
                .compact();
    }

    /**
     * JWT 검증
     */
    public String validToken(String token) {
        Key secretKey = getSigningKey(secret);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
