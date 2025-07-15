package lostark.todo.domain.logs.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.logs.dto.SaveEtcLogRequest;
import lostark.todo.domain.logs.dto.GetLogsProfitRequest;
import lostark.todo.domain.logs.dto.LogProfitResponse;
import lostark.todo.domain.logs.dto.LogsSearchParams;
import lostark.todo.domain.logs.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/logs")
@Api(tags = {"로그 API"})
public class LogsApi {

    private final LogService service;

    @ApiOperation(value = "최근 Logs 불러오기", notes = "최근 100개 단위")
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username,
                                    @Valid LogsSearchParams params) {
        return new ResponseEntity<>(service.search(username, params), HttpStatus.OK);
    }

    @ApiOperation(value = "날짜별 숙제 수익", notes = "기본 2주", response = LogProfitResponse.class)
    @GetMapping("/profit")
    public ResponseEntity<?> getLogsProfit(@AuthenticationPrincipal String username,
                                           @Valid GetLogsProfitRequest request) {
        return new ResponseEntity<>(service.getProfit(username, request), HttpStatus.OK);
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String username,
                                    @PathVariable Long logId) {
        service.delete(username, logId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "기타 수익 추가")
    @PostMapping()
    public ResponseEntity<?> saveEtcLog(@AuthenticationPrincipal String username,
                                     @Valid @RequestBody SaveEtcLogRequest request) {
        service.saveEtcLog(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
