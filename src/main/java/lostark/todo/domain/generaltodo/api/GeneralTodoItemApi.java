package lostark.todo.domain.generaltodo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.CreateGeneralTodoItemRequest;
import lostark.todo.domain.generaltodo.dto.GeneralTodoItemResponse;
import lostark.todo.domain.generaltodo.dto.SearchGeneralTodoRequest;
import lostark.todo.domain.generaltodo.dto.UpdateGeneralTodoItemRequest;
import lostark.todo.domain.generaltodo.dto.UpdateGeneralTodoItemStatusRequest;
import lostark.todo.domain.generaltodo.service.GeneralTodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/general-todos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Api(tags = {"일반 할 일 - 할 일 API"})
public class GeneralTodoItemApi {

    private final GeneralTodoService generalTodoService;

    @ApiOperation("할 일 검색")
    @GetMapping("/search")
    public ResponseEntity<List<GeneralTodoItemResponse>> search(
            @AuthenticationPrincipal String username,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long statusId) {
        SearchGeneralTodoRequest request = new SearchGeneralTodoRequest();
        request.setQuery(query);
        request.setFolderId(folderId);
        request.setCategoryId(categoryId);
        request.setStatusId(statusId);
        return new ResponseEntity<>(generalTodoService.search(username, request), HttpStatus.OK);
    }

    @ApiOperation("할 일 생성")
    @PostMapping("/items")
    public ResponseEntity<GeneralTodoItemResponse> createItem(@AuthenticationPrincipal String username,
                                                              @Valid @RequestBody CreateGeneralTodoItemRequest request) {
        return new ResponseEntity<>(generalTodoService.createItem(username, request), HttpStatus.CREATED);
    }

    @ApiOperation("할 일 수정")
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<GeneralTodoItemResponse> updateItem(@AuthenticationPrincipal String username,
                                                              @PathVariable Long itemId,
                                                              @Valid @RequestBody UpdateGeneralTodoItemRequest request) {
        return new ResponseEntity<>(generalTodoService.updateItem(username, itemId, request), HttpStatus.OK);
    }

    @ApiOperation("할 일 상태 변경")
    @PatchMapping("/items/{itemId}/status")
    public ResponseEntity<Void> updateStatus(@AuthenticationPrincipal String username,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody UpdateGeneralTodoItemStatusRequest request) {
        generalTodoService.updateItemStatus(username, itemId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("할 일 삭제")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@AuthenticationPrincipal String username,
                                           @PathVariable Long itemId) {
        generalTodoService.deleteItem(username, itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
