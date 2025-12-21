package lostark.todo.domainMyGame.myevent.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainMyGame.common.dto.ApiResponse;
import lostark.todo.domainMyGame.common.dto.PaginationResponse;
import lostark.todo.domainMyGame.myevent.dto.MyEventResponse;
import lostark.todo.domainMyGame.myevent.service.MyEventService;
import lostark.todo.domainMyGame.myevent.enums.MyEventType;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/events")
@Api(tags = {"이벤트 API"})
public class MyEventApi {

    private final MyEventService myEventService;

    @ApiOperation(value = "이벤트 목록 조회", response = ApiResponse.class)
    @GetMapping
    public ResponseEntity<?> getEvents(
            @ApiParam(value = "게임 ID 목록 (쉼표로 구분)") @RequestParam(required = false) List<Long> gameIds,
            @ApiParam(value = "시작 날짜 (ISO 8601 형식)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @ApiParam(value = "종료 날짜 (ISO 8601 형식)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @ApiParam(value = "이벤트 타입") @RequestParam(required = false) MyEventType type,
            @ApiParam(value = "페이지 번호 (기본값: 1)") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "페이지당 항목 수 (기본값: 100)") @RequestParam(defaultValue = "100") int limit) {

        Page<MyEventResponse> eventsPage = myEventService.searchEvents(
                gameIds, startDate, endDate, type, page, limit
        );
        PaginationResponse pagination = PaginationResponse.from(eventsPage);

        return new ResponseEntity<>(
                ApiResponse.success(eventsPage.getContent(), pagination),
                HttpStatus.OK
        );
    }

    @ApiOperation(value = "이벤트 상세 조회", response = ApiResponse.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(
            @ApiParam(value = "이벤트 ID", required = true) @PathVariable Long id) {

        MyEventResponse event = myEventService.getEventById(id);

        return new ResponseEntity<>(
                ApiResponse.success(event),
                HttpStatus.OK
        );
    }
}
