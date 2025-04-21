package lostark.todo.controller.apiV4.schedule;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.schedule.*;
import lostark.todo.domain.schedule.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/schedule")
@Api(tags = {"일정 API"})
public class ScheduleController {

    private final ScheduleService scheduleService;

    @ApiOperation(value = "일정 삭제 API")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> remove(@AuthenticationPrincipal String username, @PathVariable long scheduleId) {
        scheduleService.remove(username, scheduleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "일정 깐부 추가/삭제 API")
    @PostMapping("/{scheduleId}/friend")
    public ResponseEntity<?> editFriend(@AuthenticationPrincipal String username,
                                       @RequestBody EditScheduleFriendRequest request, @PathVariable long scheduleId) {
        scheduleService.editFriend(username, request, scheduleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
