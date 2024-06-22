package lostark.todo.controller.admin;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.adminDto.DashboardMemberResponse;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dash-board")
@RequiredArgsConstructor
public class AdminDashBoardController {

    private final MemberService memberService;

    @ApiOperation(value = "일일 가입자 수 통계 호출",
            response = DashboardMemberResponse.class)
    @GetMapping("/member")
    public ResponseEntity<?> searchDashBoard(@RequestParam(required = false, defaultValue = "14") int limit) {
        return new ResponseEntity<>(memberService.searchDashBoard(limit), HttpStatus.OK);
    }
}
