package lostark.todo.controller.apiV4.comment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.commentsDto.CommentRequestDto;
import lostark.todo.domain.comments.Comments;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CommentsService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
