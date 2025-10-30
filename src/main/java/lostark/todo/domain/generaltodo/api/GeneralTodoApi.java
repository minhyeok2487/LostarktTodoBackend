package lostark.todo.domain.generaltodo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.generaltodo.dto.*;
import lostark.todo.domain.generaltodo.service.GeneralTodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/general-todos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Validated
@Api(tags = {"일반 할 일 API"})
public class GeneralTodoApi {

    private final GeneralTodoService generalTodoService;

    @ApiOperation("일반 할 일 전체 조회")
    @GetMapping
    public ResponseEntity<GeneralTodoOverviewResponse> getOverview(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(generalTodoService.getOverview(username), HttpStatus.OK);
    }

    @ApiOperation("폴더 생성")
    @PostMapping("/folders")
    public ResponseEntity<GeneralTodoFolderResponse> createFolder(@AuthenticationPrincipal String username,
                                                                  @Valid @RequestBody CreateGeneralTodoFolderRequest request) {
        return new ResponseEntity<>(generalTodoService.createFolder(username, request), HttpStatus.CREATED);
    }

    @ApiOperation("폴더 이름 변경")
    @PatchMapping("/folders/{folderId}")
    public ResponseEntity<GeneralTodoFolderResponse> renameFolder(@AuthenticationPrincipal String username,
                                                                  @PathVariable Long folderId,
                                                                  @Valid @RequestBody UpdateGeneralTodoFolderRequest request) {
        return new ResponseEntity<>(generalTodoService.renameFolder(username, folderId, request), HttpStatus.OK);
    }

    @ApiOperation("폴더 순서 변경")
    @PatchMapping("/folders/reorder")
    public ResponseEntity<Void> reorderFolders(@AuthenticationPrincipal String username,
                                               @Valid @RequestBody ReorderGeneralTodoFoldersRequest request) {
        generalTodoService.reorderFolders(username, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("폴더 삭제")
    @DeleteMapping("/folders/{folderId}")
    public ResponseEntity<Void> deleteFolder(@AuthenticationPrincipal String username,
                                             @PathVariable Long folderId) {
        generalTodoService.deleteFolder(username, folderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("카테고리 생성")
    @PostMapping("/categories/folders/{folderId}")
    public ResponseEntity<GeneralTodoCategoryResponse> createCategory(@AuthenticationPrincipal String username,
                                                                      @PathVariable Long folderId,
                                                                      @Valid @RequestBody CreateGeneralTodoCategoryRequest request) {
        return new ResponseEntity<>(generalTodoService.createCategory(username, folderId, request), HttpStatus.CREATED);
    }

    @ApiOperation("카테고리 수정")
    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<GeneralTodoCategoryResponse> updateCategory(@AuthenticationPrincipal String username,
                                                                      @PathVariable Long categoryId,
                                                                      @Valid @RequestBody UpdateGeneralTodoCategoryRequest request) {
        return new ResponseEntity<>(generalTodoService.updateCategory(username, categoryId, request), HttpStatus.OK);
    }

    @ApiOperation("카테고리 순서 변경")
    @PatchMapping("/categories/folders/{folderId}/reorder")
    public ResponseEntity<Void> reorderCategories(@AuthenticationPrincipal String username,
                                                  @PathVariable Long folderId,
                                                  @Valid @RequestBody ReorderGeneralTodoCategoriesRequest request) {
        generalTodoService.reorderCategories(username, folderId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("카테고리 삭제")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal String username,
                                               @PathVariable Long categoryId) {
        generalTodoService.deleteCategory(username, categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @ApiOperation("할 일 완료 상태 변경")
    @PatchMapping("/items/{itemId}/toggle-completion")
    public ResponseEntity<Void> toggleCompletion(@AuthenticationPrincipal String username,
                                                 @PathVariable Long itemId,
                                                 @Valid @RequestBody UpdateGeneralTodoItemCompletionRequest request) {
        generalTodoService.updateItemCompletion(username, itemId, request);
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
