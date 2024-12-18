package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.boardsDto.*;
import lostark.todo.domain.board.boards.entity.Boards;
import lostark.todo.global.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.domain.board.boards.service.BoardsService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/boards")
@Api(tags = {"사이트 공지사항"})
public class BoardsController {

    private final BoardsService boardsService;

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

    @ApiOperation(value = "사이트 공지사항 id로 가져오기", response = BoardResponseDto.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        if (id < 0) {
            throw new CustomIllegalArgumentException("사이트 공지사항 id로 가져오기 에러", "id는 0보다 커야합니다.", null);
        }
        return new ResponseEntity<>(new BoardResponseDto().toDto(boardsService.findById(id)), HttpStatus.OK);
    }
}
