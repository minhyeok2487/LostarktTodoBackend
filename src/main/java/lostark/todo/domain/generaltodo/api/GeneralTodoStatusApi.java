package lostark.todo.domain.generaltodo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.CreateGeneralTodoStatusRequest;
import lostark.todo.domain.generaltodo.dto.GeneralTodoStatusResponse;
import lostark.todo.domain.generaltodo.dto.ReorderGeneralTodoStatusesRequest;
import lostark.todo.domain.generaltodo.dto.UpdateGeneralTodoStatusRequest;
import lostark.todo.domain.generaltodo.service.GeneralTodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/general-todos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Api(tags = {"일반 할 일 - 상태 API"})
public class GeneralTodoStatusApi {

    private final GeneralTodoService generalTodoService;

    @ApiOperation("상태 생성")
    @PostMapping("/categories/{categoryId}/statuses")
    public ResponseEntity<GeneralTodoStatusResponse> createStatus(@AuthenticationPrincipal String username,
                                                                  @PathVariable Long categoryId,
                                                                  @Valid @RequestBody CreateGeneralTodoStatusRequest request) {
        return new ResponseEntity<>(generalTodoService.createStatus(username, categoryId, request), HttpStatus.CREATED);
    }

    @ApiOperation("상태 이름 변경")
    @PatchMapping("/categories/{categoryId}/statuses/{statusId}")
    public ResponseEntity<GeneralTodoStatusResponse> renameStatus(@AuthenticationPrincipal String username,
                                                                  @PathVariable Long categoryId,
                                                                  @PathVariable Long statusId,
                                                                  @Valid @RequestBody UpdateGeneralTodoStatusRequest request) {
        return new ResponseEntity<>(generalTodoService.renameStatus(username, categoryId, statusId, request), HttpStatus.OK);
    }

    @ApiOperation("상태 순서 변경")
    @PatchMapping("/categories/{categoryId}/statuses/reorder")
    public ResponseEntity<Void> reorderStatuses(@AuthenticationPrincipal String username,
                                                @PathVariable Long categoryId,
                                                @Valid @RequestBody ReorderGeneralTodoStatusesRequest request) {
        generalTodoService.reorderStatuses(username, categoryId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("상태 삭제")
    @DeleteMapping("/categories/{categoryId}/statuses/{statusId}")
    public ResponseEntity<Void> deleteStatus(@AuthenticationPrincipal String username,
                                             @PathVariable Long categoryId,
                                             @PathVariable Long statusId) {
        generalTodoService.deleteStatus(username, categoryId, statusId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
