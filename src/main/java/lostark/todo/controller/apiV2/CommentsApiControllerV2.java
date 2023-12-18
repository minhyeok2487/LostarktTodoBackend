package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.service.CommentsService;
import lostark.todo.service.MemberService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v2/comments")
@Api(tags = {"방명록 API"})
public class CommentsApiControllerV2 {

    private final CommentsService commentsService;
    private final MemberService memberService;

//    @ApiOperation(value = "최상단 Comments 10개씩 불러오기")
//    @GetMapping()
//    public ResponseEntity<?> getComments(@RequestParam(value="page") int page) {
//        Page<Comments> allComments = commentsService.findAllByParentIdIsNull(page-1);
//        List<CommentResponseDto> commentResponseDtoList = allComments.stream()
//                .map(comments -> new CommentResponseDto().createResponseDto(comments))
//                .collect(Collectors.toList());
//
//        int totalPages = allComments.getTotalPages();
//        System.out.println("commentResponseDtoList = " + commentResponseDtoList);
//
//        return new ResponseEntity<>(new CommentListDto(commentResponseDtoList, totalPages), HttpStatus.OK);
//    }
}
