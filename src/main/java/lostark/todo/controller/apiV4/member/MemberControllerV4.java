package lostark.todo.controller.apiV4.member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.member.EditMainCharacter;
import lostark.todo.controller.dtoV2.member.EditProvider;
import lostark.todo.controller.dtoV2.member.MemberResponse;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.FriendsService;
import lostark.todo.service.MemberService;
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

    @ApiOperation(value = "회원 정보 조회 API",
            response = MemberResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        Member member = memberService.findMemberAndCharacters(username);
        MemberResponse memberResponse = new MemberResponse(member);
        if (memberResponse.getUsername().equals(TEST_USERNAME)) {
            memberResponse.setUsername(null);
        }
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "대표 캐릭터 변경 API")
    @PatchMapping("/main-character")
    public ResponseEntity<?> editMainCharacter(@AuthenticationPrincipal String username,
                                               @RequestBody EditMainCharacter editMainCharacter) {
        memberService.editMainCharacter(username, editMainCharacter);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @ApiOperation(value = "소셜 로그인 -> 일반 로그인 변경")
    @PatchMapping("/provider")
    public ResponseEntity<?> editProvider(@AuthenticationPrincipal String username,
                                          @RequestBody EditProvider editProvider) {
        if (username.equals(TEST_USERNAME)) {
            throw new IllegalArgumentException("테스트 계정은 변경 할 수 없습니다.");
        }
        memberService.editProvider(username, editProvider);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @ApiOperation(value = "등록 캐릭터 삭제")
    @DeleteMapping("/characters")
    public ResponseEntity<?> deleteCharacters(@AuthenticationPrincipal String username) {
        if (username.equals(TEST_USERNAME)) {
            throw new IllegalArgumentException("테스트 계정은 삭제할 수 없습니다.");
        }
        Member member = memberService.findMemberAndCharacters(username);

        if (member.getCharacters().isEmpty()) {
            throw new IllegalStateException("등록된 캐릭터가 없습니다.");
        }
        characterService.deleteByMember(member);
        friendsService.deleteByMember(member);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
