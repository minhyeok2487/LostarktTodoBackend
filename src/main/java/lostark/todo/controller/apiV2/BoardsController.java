package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.boardsDto.BoardDto;
import lostark.todo.controller.dto.boardsDto.BoardListDto;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.member.Member;
import lostark.todo.service.BoardsService;
import lostark.todo.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v2/boards")
@Api(tags = {"공지사항 (게시판) api"})
public class BoardsController {

    private final MemberService memberService;
    private final BoardsService boardsService;

    @ApiOperation(value = "메인 공지 + 게시판 전체글 10개씩 가져오기")
    @GetMapping("")
    public ResponseEntity<?> getAllNotNotice(@RequestParam(value="page") int page) {
        Page<Boards> all = boardsService.findAll(page-1);
        List<BoardDto> boardDtoList = all
                .stream().map(board -> new BoardDto().toDto(board))
                .collect(Collectors.toList());
        int totalPages = all.getTotalPages();

        List<BoardDto> noticeList = boardsService.findAllByNoticeIsTrue()
                .stream().map(board -> new BoardDto().toDto(board))
                .collect(Collectors.toList());

        BoardListDto response = new BoardListDto(boardDtoList, totalPages, noticeList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "글 하나 가져오기")
    @GetMapping("/{no}")
    public ResponseEntity<?> get(@PathVariable long no) {
        return new ResponseEntity<>(new BoardDto().toDto(boardsService.find(no)), HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 저장")
    @PostMapping()
    public ResponseEntity<?> save(@AuthenticationPrincipal String username,
                                  @RequestBody BoardDto boardDto) {
        Member member = memberService.findMember(username);

        Boards save = boardsService.save(new BoardDto().toEntityDefault(boardDto, member));

        return new ResponseEntity<>(new BoardDto().toDto(save), HttpStatus.CREATED);
    }
}
