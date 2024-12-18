package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.boardsDto.*;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.board.boards.entity.Boards;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.global.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.domain.board.boards.service.BoardImagesService;
import lostark.todo.domain.board.boards.service.BoardsService;
import lostark.todo.domain.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/boards")
@Api(tags = {"사이트 공지사항"})
public class BoardsController {

    private final MemberService memberService;
    private final BoardsService boardsService;
    private final BoardImagesService boardImagesService;

    @ApiOperation(value = "사이트 공지사항 page, size 크기로 가져오기",
            notes = "sort : 작성일 최근순, page : 1부터 시작",
            response = BoardsDto.class)
    @GetMapping()
    public ResponseEntity<?> findAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "5") int size) {
        if (page < 1) {
            throw new CustomIllegalArgumentException("사이트 공지사항 가져오기 에러", "page 입력 값은 1보다 커야 합니다.", null);
        }
        if (size < 1) {
            throw new CustomIllegalArgumentException("사이트 공지사항 가져오기 에러", "size 입력 값은 1보다 커야 합니다.", null);
        }

        Page<Boards> all = boardsService.findAll(page - 1, size);
        List<BoardResponseDto> boardResponseDtoList = all
                .stream().map(board -> new BoardResponseDto().toDto(board))
                .collect(Collectors.toList());
        int totalPages = all.getTotalPages();

        BoardsDto boardsDto = new BoardsDto().toDto(boardResponseDtoList, totalPages, page);
        return new ResponseEntity<>(boardsDto, HttpStatus.OK);
    }


    @ApiOperation(value = "메인 공지 + 게시판 전체글 10개씩 가져오기", response = BoardListDto.class)
    @GetMapping("/default")
    public ResponseEntity<?> getAllNotNotice(@RequestParam(value="page") int page) {
        log.info(String.valueOf(page));
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

    @ApiOperation(value = "사이트 공지사항 id로 가져오기", response = BoardResponseDto.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        if (id < 0) {
            throw new CustomIllegalArgumentException("사이트 공지사항 id로 가져오기 에러", "id는 0보다 커야합니다.", null);
        }
        return new ResponseEntity<>(new BoardResponseDto().toDto(boardsService.findById(id)), HttpStatus.OK);
    }

    @ApiOperation(value = "사이트 공지사항 저장",
            notes = "어드민 권한 필요",
            response = BoardResponseDto.class)
    @PostMapping()
    public ResponseEntity<?> save(@AuthenticationPrincipal String username,
                                  @RequestBody BoardInsertDto boardInsertDto) {
        Member member = memberService.get(username);

        if (member.getRole().equals(Role.ADMIN)) {
            Boards entity = Boards.builder()
                    .member(member)
                    .title(boardInsertDto.getTitle())
                    .content(boardInsertDto.getContent())
                    .boardImages(new ArrayList<>())
                    .views(0)
                    .build();

            Boards save = boardsService.save(entity);
            boardImagesService.saveByfileNames(boardInsertDto.getFileNames(), save);

            log.info("사이트 공지사항을 성공적으로 저장하였습니다. Id: {}", save.getId());
            return new ResponseEntity<>(new BoardResponseDto().toDto(save), HttpStatus.CREATED);
        } else {
            throw new CustomIllegalArgumentException("사이트 공지사항 저장 에러", "권한이 없습니다.", member);
        }
    }

    @ApiOperation(value = "사이트 공지사항 수정",
            notes = "어드민 권한 필요",
            response = BoardResponseDto.class)
    @PatchMapping()
    public ResponseEntity<?> update(String username, BoardUpdateDto boardUpdateDto) {
        Member member = memberService.get(username);

        if (member.getRole().equals(Role.ADMIN)) {
            Boards update = boardsService.update(boardUpdateDto);
            log.info("사이트 공지사항을 성공적으로 수정하였습니다. Id: {}, 수정자 : {}", update.getId(), username);
            return new ResponseEntity<>(new BoardResponseDto().toDto(update), HttpStatus.OK);
        } else {
            throw new CustomIllegalArgumentException("사이트 공지사항 수정 에러", "권한이 없습니다.", member);
        }
    }
}
