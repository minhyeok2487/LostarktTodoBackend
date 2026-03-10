package lostark.todo.domain.content.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.content.dto.WeekContentCategoryResponse;
import lostark.todo.domain.content.enums.WeekContentCategory;
import lostark.todo.domain.content.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/content/week")
@Api(tags = {"주간 콘텐츠 API"})
public class WeekContentApi {

    private final ContentService contentService;

    @ApiOperation(value = "레이드 카테고리 리스트 API")
    @GetMapping("/raid/category")
    public ResponseEntity<?> getScheduleRaidCategory() {
        return new ResponseEntity<>(contentService.getScheduleRaidCategory(), HttpStatus.OK);
    }

    @ApiOperation(value = "주간 콘텐츠 난이도 카테고리 목록 API")
    @GetMapping("/categories")
    public ResponseEntity<List<WeekContentCategoryResponse>> getWeekContentCategories() {
        List<WeekContentCategoryResponse> categories = Arrays.stream(WeekContentCategory.values())
                .sorted(Comparator.comparingInt(WeekContentCategory::getSortOrder))
                .map(WeekContentCategoryResponse::from)
                .collect(Collectors.toList());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
