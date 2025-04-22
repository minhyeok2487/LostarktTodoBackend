package lostark.todo.domain.member.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.dto.SaveCharacterRequest;
import lostark.todo.domain.member.dto.ResetPasswordRequest;
import lostark.todo.domain.member.dto.EditMainCharacterRequest;
import lostark.todo.domain.member.dto.EditProviderRequest;
import lostark.todo.domain.member.dto.MemberResponse;
import lostark.todo.domain.member.dto.SaveAdsRequest;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.customAnnotation.NotTestMember;
import lostark.todo.global.event.entity.GenericEvent;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

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

    @ApiOperation(value = "소셜 로그인 -> 일반 로그인 변경")
    @PatchMapping("/provider")
    @NotTestMember
    public ResponseEntity<?> editProvider(@AuthenticationPrincipal String username,
                                          @RequestBody EditProviderRequest request) {
        memberService.editProvider(username, request.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "광고 제거 기능 신청")
    @PostMapping("/ads")
    @NotTestMember
    public ResponseEntity<?> saveAds(
            @AuthenticationPrincipal String username,
            @RequestBody @Valid SaveAdsRequest request) {
        memberService.saveAds(username, request);

        String subject = "광고 제거 기능 신청";
        String content = String.format("신청 이메일: %s, 입금자: %s", request.getMail(), request.getName());

        eventPublisher.publishEvent(new GenericEvent(
                eventPublisher,
                subject,
                content,
                username
        ));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
