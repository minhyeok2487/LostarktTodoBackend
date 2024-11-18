package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.commentsDto.CommentListDto;
import lostark.todo.controller.dto.commentsDto.CommentRequestDto;
import lostark.todo.controller.dto.commentsDto.CommentResponseDto;
import lostark.todo.domainV2.board.comments.entity.Comments;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.board.comments.service.CommentsService;
import lostark.todo.domainV2.member.service.MemberService;
import lostark.todo.service.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final MemberService memberService;
    private final NotificationService notificationService;

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

    @ApiOperation(value = "comment 저장")
    @PostMapping()
    public ResponseEntity<?> saveComments(@AuthenticationPrincipal String username,
                                       @RequestBody CommentRequestDto commentRequestDto) {
        Member member = memberService.get(username);
        Comments updateComments = Comments.builder()
                .body(commentRequestDto.getBody())
                .member(member)
                .parentId(commentRequestDto.getParentId())
                .build();
        commentsService.save(updateComments);

        if (commentRequestDto.getParentId() != 0) { // 루트 코멘트가 아닐때 알림에 추가
            Comments comments = commentsService.findById(commentRequestDto.getParentId());
            notificationService.saveComment(comments);
        }
        // 어드민 알림 추가
        Member admin = memberService.get("repeat2487@gmail.com");
        notificationService.saveCommentAdmin(updateComments, admin);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "comment 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComments(@AuthenticationPrincipal String username, @PathVariable int commentId) {
        Member member = memberService.get(username);
        Comments updateComments = Comments.builder()
                .id(commentId)
                .member(member)
                .build();
        commentsService.delete(updateComments); //삭제

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
