package lostark.todo.domain.generaltodo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.CreateGeneralTodoFolderRequest;
import lostark.todo.domain.generaltodo.dto.GeneralTodoFolderResponse;
import lostark.todo.domain.generaltodo.dto.ReorderGeneralTodoFoldersRequest;
import lostark.todo.domain.generaltodo.dto.UpdateGeneralTodoFolderRequest;
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
@Api(tags = {"일반 할 일 - 폴더 API"})
public class GeneralTodoFolderApi {

    private final GeneralTodoService generalTodoService;

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
}
