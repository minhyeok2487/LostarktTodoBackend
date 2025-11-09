package lostark.todo.domain.servertodo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.servertodo.dto.ServerTodoCheckRequest;
import lostark.todo.domain.servertodo.dto.ServerTodoToggleEnabledRequest;
import lostark.todo.domain.servertodo.service.ServerTodoService;
import lostark.todo.global.friendPermisson.FriendPermissionType;
import lostark.todo.global.friendPermisson.CharacterMemberQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/server-todos")
@Api(tags = {"서버 공통 숙제 API"})
public class ServerTodoApi {

    private final ServerTodoService serverTodoService;
    private final CharacterMemberQueryService characterMemberQueryService;

    @ApiOperation("서버 공통 숙제 조회")
    @GetMapping
    public ResponseEntity<?> getServerTodos(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername) {
        String targetUsername = characterMemberQueryService.getUpdateMember(username, friendUsername,
                FriendPermissionType.SHOW).getUsername();
        return new ResponseEntity<>(serverTodoService.getServerTodos(targetUsername), HttpStatus.OK);
    }

    @ApiOperation("서버 공통 숙제 on/off")
    @PatchMapping("/{todoId}/toggle-enabled")
    public ResponseEntity<?> toggleEnabled(@AuthenticationPrincipal String username,
                                              @RequestParam(required = false) String friendUsername,
                                              @PathVariable Long todoId,
                                              @Valid @RequestBody ServerTodoToggleEnabledRequest request) {
        String targetUsername = characterMemberQueryService.getUpdateMember(username, friendUsername,
                FriendPermissionType.UPDATE_SETTING).getUsername();
        return new ResponseEntity<>(serverTodoService.toggleEnabled(targetUsername, todoId, request), HttpStatus.OK);
    }

    @ApiOperation("서버 공통 숙제 체크 여부 변경")
    @PostMapping("/{todoId}/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal String username,
                                      @RequestParam(required = false) String friendUsername,
                                      @PathVariable Long todoId,
                                      @Valid @RequestBody ServerTodoCheckRequest request) {
        String targetUsername = characterMemberQueryService.getUpdateMember(username, friendUsername,
                FriendPermissionType.CHECK_WEEK_TODO).getUsername();
        return new ResponseEntity<>(serverTodoService.updateChecked(targetUsername, todoId, request), HttpStatus.OK);
    }
}
