package lostark.todo.controller.apiV4.member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.dto.SaveCharacterRequest;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.friend.service.FriendsService;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.lostark.client.LostarkApiClient;
import lostark.todo.domain.character.service.CustomTodoService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lostark.todo.global.Constant.TEST_USERNAME;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/member")
@Api(tags = {"회원 캐릭터 리스트"})
public class MemberControllerV4 {

    private final MemberService memberService;
    private final CharacterService characterService;
    private final FriendsService friendsService;
    private final LostarkApiClient lostarkApiClient;
    private final CustomTodoService customTodoService;

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
