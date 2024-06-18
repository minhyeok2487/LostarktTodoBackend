package lostark.todo.controller.admin;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.member.MemberResponse;
import lostark.todo.domain.Role;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @ApiOperation(value = "어드민 API",
            response = MemberResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username) {
        Member member = memberService.findMemberAndCharacters(username);
        if(!member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
        return new ResponseEntity<>(memberService.findAll(), HttpStatus.OK);
    }
}
