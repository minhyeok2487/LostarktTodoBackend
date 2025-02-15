package lostark.todo.domain.logs.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.logs.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/logs")
@Api(tags = {"로그 API"})
public class LogsApi {

    private final LogService service;

    @ApiOperation(value = "전체 Logs 불러오기", notes = "최근 100개 로그 출력(임시)")
    @GetMapping()
    public ResponseEntity<?> search() {
        return new ResponseEntity<>(service.search(100), HttpStatus.OK);
    }
}
