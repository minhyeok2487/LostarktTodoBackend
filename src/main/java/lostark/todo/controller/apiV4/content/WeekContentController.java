package lostark.todo.controller.apiV4.content;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.content.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v4/content/week")
@Api(tags = {"주간 콘텐츠 API"})
// TODO 추후 삭제
public class WeekContentController {

    private final ContentService contentService;

    @ApiOperation(value = "레이드 카테고리 리스트 API")
    @GetMapping("/raid/category")
    public ResponseEntity<?> getScheduleRaidCategory() {
        return new ResponseEntity<>(contentService.getScheduleRaidCategory(), HttpStatus.OK);
    }
}
