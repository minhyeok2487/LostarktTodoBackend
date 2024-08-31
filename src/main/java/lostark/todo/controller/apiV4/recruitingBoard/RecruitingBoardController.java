package lostark.todo.controller.apiV4.recruitingBoard;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.recruitingBoard.CreateRecruitingBoardRequest;
import lostark.todo.controller.dtoV2.recruitingBoard.CreateRecruitingBoardResponse;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.service.MemberService;
import lostark.todo.service.RecruitingBoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/recruiting-board")
@Api(tags = {"모집 게시판 API"})
public class RecruitingBoardController {

    private final MemberService memberService;
    private final RecruitingBoardService recruitingBoardService;

    @ApiOperation(value = "모집 게시글 작성 API")
    @PostMapping("/")
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestBody CreateRecruitingBoardRequest request) {
        // 멤버 정보 가져오기
        Member member = memberService.get(username);

        // 게시글 작성
        RecruitingBoard recruitingBoard = recruitingBoardService.create(member, request);

        return new ResponseEntity<>(new CreateRecruitingBoardResponse(recruitingBoard.getId()), HttpStatus.OK);
    }
}
