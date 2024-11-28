package lostark.todo.domain.member.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.controller.dtoV2.auth.ResetPasswordRequest;
import lostark.todo.controller.dtoV2.member.EditMainCharacterRequest;
import lostark.todo.controller.dtoV2.member.MemberResponse;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.customAnnotation.NotTestMember;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Api(tags = {"회원"})
public class MemberApi {

    private final MemberService memberService;

    @ApiOperation(value = "회원 정보 조회 API",
            response = MemberResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(MemberResponse.toDto(memberService.get(username)), HttpStatus.OK);
    }

    @ApiOperation(value = "1차 회원가입 이후 캐릭터 추가",
            notes = "대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장")
    @PostMapping("/character")
    @NotTestMember
    public ResponseEntity<?> saveCharacter(@AuthenticationPrincipal String username,
                                           @RequestBody @Valid SaveCharacterRequest request) {
        memberService.createCharacter(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "비밀번호 변경")
    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid ResetPasswordRequest request) {
        memberService.updatePassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "대표 캐릭터 변경 API")
    @PatchMapping("/main-character")
    public ResponseEntity<?> editMainCharacter(@AuthenticationPrincipal String username,
                                               @RequestBody EditMainCharacterRequest request) {
        memberService.editMainCharacter(username, request.getMainCharacter());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
