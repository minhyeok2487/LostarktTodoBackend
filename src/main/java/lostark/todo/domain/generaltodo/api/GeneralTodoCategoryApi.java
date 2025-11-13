package lostark.todo.domain.generaltodo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.CreateGeneralTodoCategoryRequest;
import lostark.todo.domain.generaltodo.dto.GeneralTodoCategoryResponse;
import lostark.todo.domain.generaltodo.dto.ReorderGeneralTodoCategoriesRequest;
import lostark.todo.domain.generaltodo.dto.UpdateGeneralTodoCategoryRequest;
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
@Api(tags = {"일반 할 일 - 카테고리 API"})
public class GeneralTodoCategoryApi {

    private final GeneralTodoService generalTodoService;

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
}
