package lostark.todo.domain.servertodo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.servertodo.dto.ServerTodoToggleEnabledRequest;
import lostark.todo.domain.servertodo.service.ServerTodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/server-todos")
@Api(tags = {"서버 공통 숙제 API"})
public class ServerTodoApi {

    private final ServerTodoService serverTodoService;

    @ApiOperation("서버 공통 숙제 조회")
    @GetMapping
    public ResponseEntity<?> getServerTodos(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(serverTodoService.getServerTodos(username), HttpStatus.OK);
    }

    @ApiOperation("서버 공통 숙제 on/off")
    @PatchMapping("/{todoId}/toggle-enabled")
    public ResponseEntity<Void> toggleEnabled(@AuthenticationPrincipal String username,
                                              @PathVariable Long todoId,
                                              @Valid @RequestBody ServerTodoToggleEnabledRequest request) {
        serverTodoService.toggleEnabled(username, todoId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
