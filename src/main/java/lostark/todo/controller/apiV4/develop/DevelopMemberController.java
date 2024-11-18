package lostark.todo.controller.apiV4.develop;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainV2.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lostark.todo.Constant.DEVELOP_USERNAME;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/develop/member")
@Api(tags = {"개발용 - 회원 API"})
public class DevelopMemberController {

    private final MemberService memberService;

    @ApiOperation(value = "특정 회원 삭제 API")
    @DeleteMapping("/{name}")
    public ResponseEntity<?> get(@AuthenticationPrincipal String username, @PathVariable String name) {
        if (name.equals(DEVELOP_USERNAME)) {
            memberService.deleteMember(name);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        throw new IllegalArgumentException("삭제가 불가능한 회원 이메일 입니다.");
    }
}
