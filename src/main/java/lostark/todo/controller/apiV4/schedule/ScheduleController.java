package lostark.todo.controller.apiV4.schedule;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.schedule.*;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.schedule.ScheduleCategory;
import lostark.todo.service.MemberService;
import lostark.todo.service.ScheduleService;
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

    private final MemberService memberService;
    private final ScheduleService scheduleService;

    @ApiOperation(value = "주간 일정 리스트 출력 API", response = WeekScheduleResponse.class)
    @GetMapping()
    public ResponseEntity<?> getWeek(GetWeekScheduleRequest request,
            @AuthenticationPrincipal String username) {
        return new ResponseEntity<>(scheduleService.getWeek(username, request), HttpStatus.OK);
    }

    @ApiOperation(value = "일정 자세히 보기 API", response = GetScheduleResponse.class)
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> get(@AuthenticationPrincipal String username, @PathVariable long scheduleId) {
        GetScheduleResponse getScheduleResponse = scheduleService.getResponse(scheduleId, username);
        if(getScheduleResponse.getScheduleCategory() == ScheduleCategory.PARTY) {
            getScheduleResponse.setFriendList(scheduleService.getLeaderScheduleId(scheduleId));
        }
        return new ResponseEntity<>(getScheduleResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "일정 저장 API")
    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestBody CreateScheduleRequest request) {
        Member member = memberService.findMemberAndCharacters(username);
        scheduleService.create(member, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "일정 수정 API")
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<?> edit(@AuthenticationPrincipal String username,
                                  @RequestBody EditScheduleRequest request,
                                  @PathVariable long scheduleId) {
        scheduleService.edit(username, request, scheduleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "일정 삭제 API")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> remove(@AuthenticationPrincipal String username, @PathVariable long scheduleId) {
        scheduleService.remove(username, scheduleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @ApiOperation(value = "일정 깐부 추가/삭제 API")
//    @PostMapping("/{scheduleId}/friend")
//    public ResponseEntity<?> editFriend(@AuthenticationPrincipal String username,
//                                       @RequestBody EditScheduleFriendRequest request, @PathVariable long scheduleId) {
//        scheduleService.editFriend(username, request, scheduleId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


}
