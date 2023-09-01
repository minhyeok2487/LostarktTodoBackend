package lostark.todo.controller.admin;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/content")
@Api(tags = {"콘텐츠 관리 api"})
public class ContentApiController {

    private final ContentService contentService;

    @GetMapping()
    public ResponseEntity getContent() {
        return new ResponseEntity(contentService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/weekContent")
    public ResponseEntity addContent(@RequestBody @Valid WeekContentDto weekContentDto) {
        WeekContent weekContent = weekContentDto.toEntity();
        return new ResponseEntity(contentService.save(weekContent), HttpStatus.OK);
    }
}
