package lostark.todo.domain.admin.api;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.board.comments.service.CommentsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/comments")
@RequiredArgsConstructor
public class AdminCommentsApi {

    private final CommentsService commentsService;

    @ApiOperation(value = "어드민 댓글 목록 조회")
    @GetMapping
    public ResponseEntity<?> getCommentList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        return new ResponseEntity<>(commentsService.getCommentsForAdmin(pageRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "어드민 댓글 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentsService.deleteByAdmin(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
