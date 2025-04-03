package lostark.todo.domain.util.schedule.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.schedule.*;
import lostark.todo.domain.util.schedule.dto.SearchScheduleRequest;
import lostark.todo.domain.util.schedule.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/schedule")
@Api(tags = {"일정 API"})
public class ScheduleApi {

    private final ScheduleService scheduleService;

    @ApiOperation(value = "월별 일정 출력 API", response = WeekScheduleResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(SearchScheduleRequest request,
                                    @AuthenticationPrincipal String username) {
        return new ResponseEntity<>(scheduleService.search(username, request), HttpStatus.OK);
    }
}
