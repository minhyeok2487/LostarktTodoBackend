package lostark.todo.domain.member.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.character.service.CustomTodoService;
import lostark.todo.domain.friend.service.FriendsService;
import lostark.todo.domain.lostark.client.LostarkApiClient;
import lostark.todo.domain.member.dto.SaveCharacterRequest;
import lostark.todo.domain.member.dto.ResetPasswordRequest;
import lostark.todo.domain.member.dto.EditMainCharacterRequest;
import lostark.todo.domain.member.dto.EditProviderRequest;
import lostark.todo.domain.member.dto.MemberResponse;
import lostark.todo.domain.member.dto.SaveAdsRequest;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.customAnnotation.NotTestMember;
import lostark.todo.global.event.entity.GenericEvent;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static lostark.todo.global.Constant.TEST_USERNAME;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Api(tags = {"회원"})
public class MemberApi {

    private final MemberService memberService;
    private final CharacterService characterService;
    private final FriendsService friendsService;
    private final LostarkApiClient lostarkApiClient;
    private final CustomTodoService customTodoService;
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

    @ApiOperation(value = "등록 캐릭터 전체 삭제")
    @DeleteMapping("/characters")
    public ResponseEntity<?> deleteCharacters(@AuthenticationPrincipal String username) {
        if (username.equals(TEST_USERNAME)) {
            throw new ConditionNotMetException("테스트 계정은 삭제할 수 없습니다.");
        }
        Member member = memberService.get(username);

        if (member.getCharacters().isEmpty()) {
            throw new ConditionNotMetException("등록된 캐릭터가 없습니다.");
        }
        customTodoService.deleteMyMember(member);
        characterService.deleteByMember(member);
        friendsService.deleteByMember(member);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @ApiOperation(value = "회원 API KEY 갱신")
    @PatchMapping("/api-key")
    public ResponseEntity<?> updateApiKey(@AuthenticationPrincipal String username,
                                          @RequestBody SaveCharacterRequest saveCharacterRequest) {
        // 1. 검증
        Member member = memberService.get(username);
        if (saveCharacterRequest.getApiKey() == null || saveCharacterRequest.getApiKey().isEmpty()) {
            throw new ConditionNotMetException("API KEY를 입력하여 주십시오");
        }
        if (member.getApiKey() != null && member.getApiKey().equals(saveCharacterRequest.getApiKey())) {
            throw new ConditionNotMetException("동일한 API KEY입니다.");
        }

        // 2. API KEY 인증 확인
        lostarkApiClient.findEvents(saveCharacterRequest.getApiKey());

        // 3. API KEY 업데이트
        memberService.editApiKey(member, saveCharacterRequest.getApiKey());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
