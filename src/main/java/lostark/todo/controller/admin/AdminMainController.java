package lostark.todo.controller.admin;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.controller.dtoV2.member.MemberResponse;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMainController {

    private final MemberService memberService;
    private final CharacterService characterService;

    @ApiOperation(value = "어드민 회원 정보",
            response = DashboardResponse.class)
    @GetMapping("/member")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal String username) {
        Member member = memberService.get(username);
        MemberResponse memberResponse = new MemberResponse(member);
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "일일 가입자 수 통계 호출",
            response = DashboardResponse.class)
    @GetMapping("/dash-board/member")
    public ResponseEntity<?> searchMemberDashBoard(@RequestParam(required = false, defaultValue = "14") int limit) {
        return new ResponseEntity<>(memberService.searchMemberDashBoard(limit), HttpStatus.OK);
    }

    @ApiOperation(value = "일일 가입 캐릭터 수 통계 호출",
            response = DashboardResponse.class)
    @GetMapping("/dash-board/characters")
    public ResponseEntity<?> searchCharactersDashBoard(@RequestParam(required = false, defaultValue = "14") int limit) {
        return new ResponseEntity<>(characterService.searchCharactersDashBoard(limit), HttpStatus.OK);
    }
}
