package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.ChallengeContentEnum;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/characters/todo")
@Api(tags = {"원정대 숙제 Api"})
public class TodoControllerV4 {

    private final CharacterService characterService;
    private final MemberService memberService;

    @ApiOperation(value = "원정대 주간 숙제(도전어비스, 도전가디언) 수정")
    @PatchMapping("/challenge/{serverName}/{content}")
    public ResponseEntity<?> updateChallenge(@AuthenticationPrincipal String username,
                                             @PathVariable String serverName,
                                             @PathVariable ChallengeContentEnum content) {
        // username -> member 조회
        Member member = memberService.get(username);

        // 도전 어비스, 가디언 업데이트
        characterService.updateChallenge(member, serverName, content);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
