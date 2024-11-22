package lostark.todo.controller.apiV4.comment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.commentsDto.CommentRequestDto;
import lostark.todo.domainV2.board.comments.entity.Comments;
import lostark.todo.domainV2.member.entity.Member;
import lostark.todo.domainV2.board.comments.service.CommentsService;
import lostark.todo.domainV2.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/comments")
@Api(tags = {"방명록 API"})
public class CommentController {

    private final MemberService memberService;
    private final CommentsService commentsService;

    @ApiOperation(value = "comment 수정")
    @PatchMapping()
    public ResponseEntity<?> updateComments(@AuthenticationPrincipal String username,
                                            @RequestBody CommentRequestDto commentRequestDto) {
        Member member = memberService.get(username);
        Comments updateComments = Comments.builder()
                .id(commentRequestDto.getId())
                .body(commentRequestDto.getBody())
                .member(member)
                .build();
        commentsService.update(updateComments); //업데이트
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "comment Test")
    @GetMapping()
    public ResponseEntity<?> findComments() {
        return new ResponseEntity<>(commentsService.test(), HttpStatus.OK);
    }
}
