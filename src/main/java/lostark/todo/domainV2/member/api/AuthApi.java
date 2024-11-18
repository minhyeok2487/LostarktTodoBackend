package lostark.todo.domainV2.member.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.domainV2.member.service.MemberService;
import lostark.todo.global.customAnnotation.NotTestMember;
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

    private final MemberService memberService;

    @ApiOperation(value = "1차 회원가입 이후 캐릭터 추가",
            notes = "대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장")
    @PostMapping("/character")
    @NotTestMember
    public ResponseEntity<?> saveCharacter(@AuthenticationPrincipal String username,
                                           @RequestBody @Valid SaveCharacterRequest request) {
        memberService.createCharacter(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
