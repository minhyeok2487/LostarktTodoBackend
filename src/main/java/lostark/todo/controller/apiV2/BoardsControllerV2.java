package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.boardsDto.BoardResponseDto;
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
public class BoardsControllerV2 {

    private final MemberService memberService;
    private final BoardsService boardsService;

    @ApiOperation(value = "메인 공지 + 게시판 전체글 10개씩 가져오기", response = BoardListDto.class)
    @GetMapping("")
    public ResponseEntity<?> getAllNotNotice(@RequestParam(value="page") int page) {
        Page<Boards> all = boardsService.findAllByNoticeFalse(page-1);
        List<BoardResponseDto> boardResponseDtoList = all
                .stream().map(board -> new BoardResponseDto().toDto(board))
                .collect(Collectors.toList());
        int totalPages = all.getTotalPages();

        List<BoardResponseDto> noticeList = boardsService.findAllByNoticeIsTrue()
                .stream().map(board -> new BoardResponseDto().toDto(board))
                .collect(Collectors.toList());

        BoardListDto response = new BoardListDto(boardResponseDtoList, totalPages, noticeList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "글 하나 가져오기", response = BoardResponseDto.class)
    @GetMapping("/{no}")
    public ResponseEntity<?> get(@PathVariable long no) {
        return new ResponseEntity<>(new BoardResponseDto().toDto(boardsService.findById(no)), HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 저장")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT Bearer 토큰", required = true,
                    dataTypeClass = String.class, paramType = "header")
    })
    @PostMapping()
    public ResponseEntity<?> save(@AuthenticationPrincipal String username,
                                  @RequestBody BoardResponseDto boardResponseDto) {
        Member member = memberService.findMember(username);

        Boards save = boardsService.save(new BoardResponseDto().toEntityDefault(boardResponseDto, member));

        return new ResponseEntity<>(new BoardResponseDto().toDto(save), HttpStatus.CREATED);
    }
}
