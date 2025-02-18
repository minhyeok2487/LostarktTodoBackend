package lostark.todo.domain.logs.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.logs.dto.GetLogsProfitRequest;
import lostark.todo.domain.logs.dto.LogProfitResponse;
import lostark.todo.domain.logs.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/logs")
@Api(tags = {"로그 API"})
public class LogsApi {

    private final LogService service;

    @ApiOperation(value = "전체 Logs 불러오기", notes = "최근 100개 로그 출력(임시)")
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(service.search(100, username), HttpStatus.OK);
    }

    @ApiOperation(value = "날짜별 숙제 수익", notes = "기본 2주", response = LogProfitResponse.class)
    @GetMapping("/profit")
    public ResponseEntity<?> getLogsProfit(@AuthenticationPrincipal String username,
                                           @Valid GetLogsProfitRequest request) {
        return new ResponseEntity<>(service.getProfit(username, request), HttpStatus.OK);
    }
}
