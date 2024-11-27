package lostark.todo.controller.apiV4.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.global.config.TokenProvider;
import lostark.todo.controller.dtoV2.auth.ResetPasswordRequest;
import lostark.todo.controller.dtoV2.auth.SignUpRequest;
import lostark.todo.controller.dtoV2.auth.AuthResponse;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.EmailService;
import lostark.todo.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v4/auth")
@Api(tags = {"인증(회원가입, 로그인, 로그아웃) API"})
public class AuthControllerV4 {

    private final MemberService memberService;
    private final EmailService emailService;
    private final TokenProvider tokenProvider;

    // TODO 추후삭제
    @ApiOperation(value = "1차 회원 가입",
            notes="이메일, 비밀번호(O), Api-Key, 대표캐릭터(X)", response = AuthResponse.class)
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request) {

        if (!request.getPassword().equals(request.getEqualPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        emailService.isAuth(request.getMail(), request.getNumber());

        // Member 회원가입
        Member signupMember = memberService.createMember(request.getMail(), request.getPassword());

        // 회원가입 완료시 auth_mail에 저장된 인증번호 모두 삭제
        emailService.deleteAll(request.getMail());

        String token = tokenProvider.createToken(signupMember);

        return new ResponseEntity<>(new AuthResponse().toDto(signupMember, token), HttpStatus.CREATED);
    }

    // TODO 추후 삭제
    @ApiOperation(value = "비밀번호 변경")
    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid ResetPasswordRequest request) {

        emailService.isAuth(request.getMail(), request.getNumber());

        memberService.updatePassword(request);

        // 비밀번호 변경 완료시 auth_mail에 저장된 인증번호 모두 삭제
        emailService.deleteAll(request.getMail());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
