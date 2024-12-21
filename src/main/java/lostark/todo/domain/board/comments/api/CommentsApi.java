package lostark.todo.domain.board.comments.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.board.comments.service.CommentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/comments")
@Api(tags = {"방명록 API(레거시)"})
public class CommentsApi {

    private final CommentsService service;

    @ApiOperation(value = "전체 Comments 불러오기", notes = "루트 코멘트 기준 page(기본 5)개씩 불러옴")
    @GetMapping()
    public ResponseEntity<?> searchComments(@RequestParam(value="page", defaultValue = "5") int page) {
        return new ResponseEntity<>(service.searchComments(page), HttpStatus.OK);
    }
}
