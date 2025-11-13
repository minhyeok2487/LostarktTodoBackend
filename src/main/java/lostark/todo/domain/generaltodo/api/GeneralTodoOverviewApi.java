package lostark.todo.domain.generaltodo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.GeneralTodoOverviewResponse;
import lostark.todo.domain.generaltodo.service.GeneralTodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/general-todos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Api(tags = {"일반 할 일 - 기본 API"})
public class GeneralTodoOverviewApi {

    private final GeneralTodoService generalTodoService;

    @ApiOperation("일반 할 일 전체 조회")
    @GetMapping
    public ResponseEntity<GeneralTodoOverviewResponse> getOverview(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(generalTodoService.getOverview(username), HttpStatus.OK);
    }
}
