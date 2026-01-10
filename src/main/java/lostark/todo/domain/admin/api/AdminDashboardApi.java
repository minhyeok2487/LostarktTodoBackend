package lostark.todo.domain.admin.api;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.DashboardResponse;
import lostark.todo.domain.admin.dto.DashboardSummaryResponse;
import lostark.todo.domain.member.dto.MemberResponse;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.member.service.MemberService;
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
public class AdminDashboardApi {

    private final MemberService memberService;
    private final CharacterService characterService;

    @ApiOperation(value = "어드민 회원 정보",
            response = MemberResponse.class)
    @GetMapping("/member")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(MemberResponse.toDto(memberService.get(username)), HttpStatus.OK);
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

    @ApiOperation(value = "대시보드 통계 요약",
            response = DashboardSummaryResponse.class)
    @GetMapping("/api/v1/stats/summary")
    public ResponseEntity<?> getSummary() {
        return new ResponseEntity<>(memberService.getDashboardSummary(), HttpStatus.OK);
    }
}
