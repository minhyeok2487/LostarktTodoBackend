package lostark.todo.domainV2.board.comments.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainV2.board.comments.dto.CommentResponse;
import lostark.todo.domainV2.board.comments.service.CommentsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/comments")
@Api(tags = {"방명록 신버전 API"})
public class CommentsApi {

    private final CommentsService commentsService;

    @ApiOperation(value = "코멘트 불러오기 (커서 기반 페이지네이션)", response = CommentResponse.class)
    @GetMapping()
    public ResponseEntity<?> searchCursor(@RequestParam(required = false) Long commentsId,
                                          @RequestParam(required = false, defaultValue = "10") int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return new ResponseEntity<>(commentsService.searchCursor(commentsId, pageRequest), HttpStatus.OK);
    }

}
