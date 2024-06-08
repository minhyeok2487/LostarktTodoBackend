package lostark.todo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import lostark.todo.security.ApplicationOAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * JWT(JSON Web Token) 생성과 유효성 검사를 담당하는 서비스 클래스
 */
@Service
@Slf4j
public class TokenProvider {

    @Value("${JWT-SECRET-KEY}")
    private String secretKey;



    /**
     * 일반 로그인 JWT 생성
     */
    public String createToken(Member member) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secretKey)
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
        // 기한 무제한
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, key)
                .setSubject(userPrincipal.getName())
                .setIssuer("LostarkTodo")
                .setIssuedAt(new Date())
                .compact();
    }


    /**
     * JWT 검증
     */
    public String validToken(String token) {
        // parseClaimsJws메서드가 Base 64로 디코딩 및 파싱.
        // 즉, 헤더와 페이로드를 setSigningKey로 넘어온 시크릿을 이용 해 서명 후, token의 서명 과 비교.
        // 위조되지 않았다면 페이로드(Claims) 리턴, 위조라면 예외를 날림
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();

    }
}
