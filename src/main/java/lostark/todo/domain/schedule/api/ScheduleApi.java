package lostark.todo.domain.schedule.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.schedule.*;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.content.service.ContentService;
import lostark.todo.domain.schedule.dto.SearchScheduleRequest;
import lostark.todo.domain.schedule.enums.ScheduleCategory;
import lostark.todo.domain.schedule.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/schedule")
@Api(tags = {"일정 API"})
public class ScheduleApi {

    private final ScheduleService scheduleService;
    private final CharacterService characterService;
    private final ContentService contentService;

    @ApiOperation(value = "일정용 레이드 카테고리 리스트 API")
    @GetMapping("/raid/category")
    public ResponseEntity<?> getScheduleRaidCategory() {
        return new ResponseEntity<>(contentService.getScheduleRaidCategory(), HttpStatus.OK);
    }

    @ApiOperation(value = "일정 저장 API")
    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestBody CreateScheduleRequest request) {
        Character character = characterService.get(request.getLeaderCharacterId(), username);
        scheduleService.create(character, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "월별 일정 출력 API", response = WeekScheduleResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(SearchScheduleRequest request,
                                    @AuthenticationPrincipal String username) {
        return new ResponseEntity<>(scheduleService.search(username, request), HttpStatus.OK);
    }

    @ApiOperation(value = "일정 자세히 보기 API", response = GetScheduleResponse.class)
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> get(@AuthenticationPrincipal String username,
                                 @PathVariable long scheduleId,
                                 @RequestParam(required = false) Long leaderScheduleId) {
        GetScheduleResponse getScheduleResponse = scheduleService.getResponseIsReader(scheduleId, username, leaderScheduleId);
        if (getScheduleResponse.getScheduleCategory() == ScheduleCategory.PARTY) {
            Long idToUse = Optional.ofNullable(leaderScheduleId).orElse(scheduleId);
            getScheduleResponse.setFriendList(scheduleService.getLeaderScheduleId(idToUse));
        }
        return new ResponseEntity<>(getScheduleResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "일정 수정 API")
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<?> edit(@AuthenticationPrincipal String username,
                                  @RequestBody EditScheduleRequest request,
                                  @PathVariable long scheduleId) {
        scheduleService.edit(username, request, scheduleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
