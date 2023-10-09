package lostark.todo.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.todo.Todo;
import lostark.todo.service.ContentService;
import lostark.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/week-content")
@Api(tags = {"주간 컨텐츠 관리 API"})
public class WeekContentApiController {

    private final ContentService contentService;

    @ApiOperation(value = "주간 컨텐츠 전체 호출")
    @GetMapping("/")
    public ResponseEntity getWeekContent() {
        return new ResponseEntity(contentService.findAllByWeekContent(), HttpStatus.OK);
    }

    @ApiOperation(value = "주간 컨텐츠 추가")
    @PostMapping("/")
    public ResponseEntity postWeekContent(@RequestBody WeekContent weekContent) {
        return new ResponseEntity(contentService.saveWeekContent(weekContent), HttpStatus.OK);
    }

}
