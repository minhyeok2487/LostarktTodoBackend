package lostark.todo.domain.inspection.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.inspection.dto.*;
import lostark.todo.domain.inspection.service.InspectionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/inspection")
@Api(tags = {"군장검사 API"})
public class InspectionApi {

    private final InspectionService inspectionService;

    @ApiOperation(value = "군장검사 캐릭터 등록")
    @PostMapping("/characters")
    public ResponseEntity<InspectionCharacterResponse> createInspectionCharacter(
            @AuthenticationPrincipal String username,
            @RequestBody @Valid CreateInspectionCharacterRequest request) {
        return new ResponseEntity<>(inspectionService.create(username, request), HttpStatus.CREATED);
    }

    @ApiOperation(value = "군장검사 캐릭터 목록 조회")
    @GetMapping("/characters")
    public ResponseEntity<List<InspectionCharacterResponse>> getInspectionCharacters(
            @AuthenticationPrincipal String username) {
        return new ResponseEntity<>(inspectionService.getAll(username), HttpStatus.OK);
    }

    @ApiOperation(value = "군장검사 캐릭터 상세 조회 (차트 데이터 포함)")
    @GetMapping("/characters/{inspectionCharacterId}")
    public ResponseEntity<InspectionDashboardResponse> getInspectionCharacterDetail(
            @AuthenticationPrincipal String username,
            @PathVariable long inspectionCharacterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusDays(30);
        // 날짜 범위 유효성 검사
        if (startDate.isAfter(endDate)) {
            startDate = endDate.minusDays(30);
        } else if (ChronoUnit.DAYS.between(startDate, endDate) > 90) {
            startDate = endDate.minusDays(90);
        }
        return new ResponseEntity<>(
                inspectionService.getDetail(username, inspectionCharacterId, startDate, endDate),
                HttpStatus.OK);
    }

    @ApiOperation(value = "군장검사 캐릭터 설정 수정")
    @PatchMapping("/characters/{inspectionCharacterId}")
    public ResponseEntity<?> updateInspectionCharacter(
            @AuthenticationPrincipal String username,
            @PathVariable long inspectionCharacterId,
            @RequestBody UpdateInspectionCharacterRequest request) {
        inspectionService.update(username, inspectionCharacterId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "군장검사 캐릭터 삭제")
    @DeleteMapping("/characters/{inspectionCharacterId}")
    public ResponseEntity<?> deleteInspectionCharacter(
            @AuthenticationPrincipal String username,
            @PathVariable long inspectionCharacterId) {
        inspectionService.delete(username, inspectionCharacterId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "군장검사 수동 새로고침")
    @PostMapping("/characters/{inspectionCharacterId}/refresh")
    public ResponseEntity<InspectionDashboardResponse> refreshInspectionCharacter(
            @AuthenticationPrincipal String username,
            @PathVariable long inspectionCharacterId) {
        return new ResponseEntity<>(inspectionService.refresh(username, inspectionCharacterId), HttpStatus.OK);
    }

    @ApiOperation(value = "군장검사 수집 시간 조회")
    @GetMapping("/schedule")
    public ResponseEntity<Map<String, Integer>> getSchedule(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(
                Collections.singletonMap("scheduleHour", inspectionService.getScheduleHour(username)));
    }

    @ApiOperation(value = "군장검사 수집 시간 변경")
    @PatchMapping("/schedule")
    public ResponseEntity<?> updateSchedule(
            @AuthenticationPrincipal String username,
            @RequestBody @Valid UpdateInspectionScheduleRequest request) {
        inspectionService.updateScheduleHour(username, request.getScheduleHour());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
