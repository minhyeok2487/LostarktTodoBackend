package lostark.todo.controller.apiV4.recruitingBoard;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.recruitingBoard.*;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.service.MemberService;
import lostark.todo.service.RecruitingBoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/recruiting-board")
@Api(tags = {"모집 게시판 API"})
public class RecruitingBoardController {

    private final MemberService memberService;
    private final RecruitingBoardService recruitingBoardService;

    @ApiOperation(value = "모집 게시글 작성 API")
    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestBody CreateRecruitingBoardRequest request) {
        // 멤버 정보 가져오기
        Member member = memberService.get(username);

        // 게시글 작성
        RecruitingBoard recruitingBoard = recruitingBoardService.create(member, request);

        return new ResponseEntity<>(new CreateRecruitingBoardResponse(recruitingBoard.getId()), HttpStatus.OK);
    }

    @ApiOperation(value = "모집 게시글 리스트 조회", response = SearchRecruitingBoardResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(SearchRecruitingBoardRequest request,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page-1, limit);
        Page<RecruitingBoard> search = recruitingBoardService.search(request, pageRequest);
        return new ResponseEntity<>(search.map(SearchRecruitingBoardResponse::new), HttpStatus.OK);
    }

    @ApiOperation(value = "모집 게시글 조회", response = GetRecruitingBoardResponse.class)
    @GetMapping("/{recruitingBoardId}")
    public ResponseEntity<?> get(@PathVariable Long recruitingBoardId) {
        RecruitingBoard recruitingBoard = recruitingBoardService.get(recruitingBoardId);
        return new ResponseEntity<>(new GetRecruitingBoardResponse(recruitingBoard), HttpStatus.OK);
    }
}
