package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.commentsDto.CommentListDto;
import lostark.todo.controller.dto.commentsDto.CommentResponseDto;
import lostark.todo.domain.board.comments.entity.Comments;
import lostark.todo.domain.board.comments.service.CommentsService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v3/comments")
@Api(tags = {"방명록 API"})
public class CommentsController {

    private final CommentsService commentsService;

    @ApiOperation(value = "전체 Comments 불러오기", notes = "루트 코멘트 기준 page(기본 5)개씩 불러옴")
    @GetMapping()
    public ResponseEntity<?> findComments(@RequestParam(value="page", defaultValue = "5") int page) {
        Page<Comments> allComments = commentsService.findAllByParentIdIs0(page-1);
        int totalPages = allComments.getTotalPages();

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comments comment : allComments.getContent()) {
            List<Comments> commentList = commentsService.findAllByParentId(comment.getId());
            commentResponseDtoList.add(new CommentResponseDto().createResponseDto(comment));
            if(!commentList.isEmpty()) {
                for (Comments reply : commentList) {
                    commentResponseDtoList.add(new CommentResponseDto().createResponseDto(reply));
                }
            }
        }

        return new ResponseEntity<>(new CommentListDto(commentResponseDtoList, totalPages), HttpStatus.OK);
    }
}
