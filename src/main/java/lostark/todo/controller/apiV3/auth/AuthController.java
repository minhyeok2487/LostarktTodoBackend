package lostark.todo.controller.apiV3.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.auth.AuthResponseDto;
import lostark.todo.controller.dto.auth.AuthSignupDto;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.redis.RedisRepository;
import lostark.todo.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.service.AuthService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/auth")
@Api(tags = {"인증(회원가입, 로그인, 로그아웃) API"})
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;
    private final RedisRepository repository;

    @ApiOperation(value = "1차 회원 가입",
            notes="이메일, 비밀번호(O), Api-Key, 대표캐릭터(X)", response = AuthResponseDto.class)
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid AuthSignupDto authSignupDto) {
        boolean auth = repository.findAllByMail(authSignupDto.getMail()).stream()
                .anyMatch(mail -> mail.getNumber() == authSignupDto.getNumber());
        if (!auth) {
            throw new IllegalStateException("인증번호 체크가 만료되었습니다. 다시 인증해주십시오.");
        }

        if (!authSignupDto.getPassword().equals(authSignupDto.getEqualPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // Member 회원가입
        Member signupMember = memberService.createMember(authSignupDto.getMail(), authSignupDto.getPassword());

        String message = "회원가입이 정상 처리되었습니다. username:" + signupMember.getUsername();
        log.info(message);
        return new ResponseEntity<>(new AuthResponseDto(true, message), HttpStatus.CREATED);
    }

    @ApiOperation(value = "로그아웃",
            notes = "로그인 유형 상관없이 로그아웃",
            response = String.class)
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);

        AuthResponseDto authResponseDto = null;
        if (member.getAuthProvider().equals("Google")) {
            try {
                authResponseDto = authService.googleLogout(member);
            } catch (Exception e) {
                return new ResponseEntity<>(new AuthResponseDto(false, "구글 로그아웃 실패 : " + e.getMessage()), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }
}
