package lostark.todo.domain.member.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.LoginMemberRequest;
import lostark.todo.domain.member.dto.MemberResponse;
import lostark.todo.domain.member.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Api(tags = {"인증(회원가입, 로그인, 로그아웃)"})
public class AuthApi {

    private final AuthService authService;

    @ApiOperation(value = "일반 로그인",
            notes="JWT", response = MemberResponse.class)
    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@RequestBody @Valid LoginMemberRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @ApiOperation(value = "로그아웃",
            notes = "로그인 유형 상관없이 로그아웃",
            response = String.class)
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(authService.logout(username), HttpStatus.OK);
    }
}
