package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.commentsDto.CommentListDto;
import lostark.todo.controller.dto.commentsDto.CommentRequestDto;
import lostark.todo.controller.dto.commentsDto.CommentResponseDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.comments.Comments;
import lostark.todo.domain.member.Member;
import lostark.todo.service.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/comments")
@Api(tags = {"방명 API"})
public class CommentsApiController {

    private final CommentsService commentsService;
    private final MemberService memberService;

    @ApiOperation(value = "전체 Comments 불러오기")
    @GetMapping()
    public ResponseEntity<?> findComments(@AuthenticationPrincipal String username, @RequestParam(value="page") int page) {
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

        Member member = memberService.findMember(username);
        MemberResponseDto memberResponseDto = new MemberResponseDto().toDto(member);
        if (member.getRole().equals(Role.ADMIN)) {
            memberResponseDto.setUsername("관리자");
        }
        return new ResponseEntity<>(new CommentListDto(commentResponseDtoList, totalPages, memberResponseDto), HttpStatus.OK);
    }

    @ApiOperation(value = "comment 저장")
    @PostMapping()
    public ResponseEntity<?> saveComments(@AuthenticationPrincipal String username,
                                       @RequestBody CommentRequestDto commentRequestDto) {
        Member member = memberService.findMember(username);
        Comments updateComments = Comments.builder()
                .body(commentRequestDto.getBody())
                .member(member)
                .parentId(commentRequestDto.getParentId())
                .build();
        commentsService.save(updateComments);

        Page<Comments> allComments = commentsService.findAllByParentIdIs0(0);
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

        MemberResponseDto memberResponseDto = new MemberResponseDto().toDto(member);
        if (member.getRole().equals(Role.ADMIN)) {
            memberResponseDto.setUsername("관리자");
        }

        return new ResponseEntity<>( new CommentListDto(commentResponseDtoList, totalPages, memberResponseDto), HttpStatus.OK);
    }

    @ApiOperation(value = "comment 수정")
    @PatchMapping()
    public ResponseEntity updateComments(@AuthenticationPrincipal String username, @RequestBody CommentRequestDto commentRequestDto) {
        Member member = memberService.findMember(username);
        Comments updateComments = Comments.builder()
                .id(commentRequestDto.getId())
                .body(commentRequestDto.getBody())
                .member(member)
                .build();
        commentsService.update(updateComments); //업데이트

        List<Comments> allComments = commentsService.findAll();
        List<CommentResponseDto> commentResponseDtoList = allComments.stream()
                .map(comments -> new CommentResponseDto().createResponseDto(comments))
                .collect(Collectors.toList());
        return new ResponseEntity( commentResponseDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "comment 삭제")
    @DeleteMapping()
    public ResponseEntity deleteComments(@AuthenticationPrincipal String username, @RequestBody CommentRequestDto commentRequestDto) {
        Member member = memberService.findMember(username);
        Comments updateComments = Comments.builder()
                .id(commentRequestDto.getId())
                .member(member)
                .build();
        commentsService.delete(updateComments); //삭제

        List<Comments> allComments = commentsService.findAll();
        List<CommentResponseDto> commentResponseDtoList = allComments.stream()
                .map(comments -> new CommentResponseDto().createResponseDto(comments))
                .collect(Collectors.toList());
        return new ResponseEntity(commentResponseDtoList, HttpStatus.OK);
    }
}
