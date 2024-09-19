package lostark.todo.controller.admin;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsResponse;
import lostark.todo.domainV2.board.comments.service.CommentsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentsController {

    private final CommentsService commentsService;

    @ApiOperation(value = "어드민 방명록 테이블 조회 API",
            response = SearchAdminCommentsResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(SearchAdminCommentsRequest request,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page-1, limit);
        Page<SearchAdminCommentsResponse> result = commentsService.searchAdmin(request, pageRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
