package lostark.todo.controller.apiV4.schedule;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.schedule.CreateScheduleRequest;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MemberService;
import lostark.todo.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/schedule")
@Api(tags = {"일정 API"})
public class ScheduleController {

    private final MemberService memberService;
    private final ScheduleService scheduleService;

    @ApiOperation(value = "스케줄 저장 API")
    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestBody CreateScheduleRequest request) {
        Member member = memberService.findMemberAndCharacters(username);
        scheduleService.create(member, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
