package lostark.todo.controller.admin;

import io.swagger.annotations.Api;
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

    @GetMapping("/")
    public ResponseEntity getWeekContent() {
        List<WeekContent> weekContentList = contentService.findAllByWeekContent();
        return new ResponseEntity(weekContentList, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity addWeekContent(@RequestBody WeekContent weekContent) {
        WeekContent result = contentService.saveWeekContent(weekContent);
        return new ResponseEntity(result, HttpStatus.OK);
    }

}
