package lostark.todo.controller.apiV4.member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.controller.dtoV2.member.EditMainCharacterRequest;
import lostark.todo.controller.dtoV2.member.EditProviderRequest;
import lostark.todo.controller.dtoV2.member.MemberResponse;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.FriendsService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lostark.todo.Constant.TEST_USERNAME;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/member")
@Api(tags = {"회원 캐릭터 리스트"})
public class MemberControllerV4 {

    private final MemberService memberService;
    private final CharacterService characterService;
    private final FriendsService friendsService;
    private final LostarkApiService lostarkApiService;

    @ApiOperation(value = "회원 정보 조회 API",
            response = MemberResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        Member member = memberService.get(username);
        MemberResponse memberResponse = new MemberResponse(member);
        if (memberResponse.getUsername().equals(TEST_USERNAME)) {
            memberResponse.setUsername(null);
        }
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
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
    public ResponseEntity<?> editProvider(@AuthenticationPrincipal String username,
                                          @RequestBody EditProviderRequest request) {
        memberService.editProvider(username, request.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "등록 캐릭터 전체 삭제")
    @DeleteMapping("/characters")
    public ResponseEntity<?> deleteCharacters(@AuthenticationPrincipal String username) {
        if (username.equals(TEST_USERNAME)) {
            throw new IllegalArgumentException("테스트 계정은 삭제할 수 없습니다.");
        }
        Member member = memberService.get(username);

        if (member.getCharacters().isEmpty()) {
            throw new IllegalStateException("등록된 캐릭터가 없습니다.");
        }
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
            throw new IllegalArgumentException("API KEY를 입력하여 주십시오");
        }
        if (member.getApiKey() != null && member.getApiKey().equals(saveCharacterRequest.getApiKey())) {
            throw new IllegalArgumentException("동일한 API KEY입니다.");
        }

        // 2. API KEY 인증 확인
        lostarkApiService.findEvents(saveCharacterRequest.getApiKey());

        // 3. API KEY 업데이트
        memberService.editApiKey(member, saveCharacterRequest.getApiKey());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
