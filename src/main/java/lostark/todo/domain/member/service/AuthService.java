package lostark.todo.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.LoginMemberRequest;
import lostark.todo.domain.member.dto.MemberResponse;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.global.config.TokenProvider;
import lostark.todo.global.dto.GlobalResponseDto;
import lostark.todo.domain.member.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static lostark.todo.global.exhandler.ErrorMessageConstants.LOGIN_FAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 일반 로그인
    @Transactional
    public MemberResponse login(LoginMemberRequest request) {
        Member member = validateLogin(request);

        return MemberResponse.builder()
                .username(member.getUsername())
                .token(tokenProvider.createToken(member))
                .build();
    }

    // 로그인 검증
    @Transactional
    public Member validateLogin(LoginMemberRequest request) {
        Member member = memberRepository.get(request.getUsername());
        if (passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new IllegalArgumentException(LOGIN_FAIL);
        }
    }

    // 로그아웃
    public GlobalResponseDto logout(String username) {
        return Optional.ofNullable(memberRepository.get(username))
                .map(member -> {
                    switch (member.getAuthProvider()) {
                        case "Google":
                            try {
                                return googleLogout(member);
                            } catch (Exception e) {
                                return new GlobalResponseDto(false, "구글 로그아웃 실패 : " + e.getMessage());
                            }
                        case "none":
                            return new GlobalResponseDto(true, "로그아웃 성공");
                        default:
                            return new GlobalResponseDto(false, "알 수 없는 인증 제공자");
                    }
                })
                .orElseGet(() -> new GlobalResponseDto(false, "사용자를 찾을 수 없습니다"));
    }

    public GlobalResponseDto googleLogout(Member member) throws Exception {

        // 토큰 취소 URL
        String revokeUrl = "https://accounts.google.com/o/oauth2/revoke";

        // URl 연결
        URL url = new URL(revokeUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // POST 매핑
        connection.setRequestMethod("POST");

        // Body 생성
        String requestBody = "token=" + member.getAccessKey();
        connection.setRequestProperty("Content-Length", String.valueOf(requestBody.length()));

        // input/output 스트림 true
        connection.setDoOutput(true);

        // Body 작성
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        // 닫기
        connection.disconnect();
        return new GlobalResponseDto(true, "구글 로그아웃 성공");
    }
}
