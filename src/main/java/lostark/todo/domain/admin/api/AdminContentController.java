package lostark.todo.domain.admin.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.admin.dto.AddContentRequest;
import lostark.todo.domain.content.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/api/v1/content")
@Api(tags = {"어드민 - 컨텐츠 추가"})
public class AdminContentController {

    private final ContentService contentService;

    @ApiOperation(value = "컨텐츠 추가")
    @PostMapping("")
    public ResponseEntity<?> addContent(@RequestBody @Valid AddContentRequest request) {
        return new ResponseEntity<>(contentService.addContent(request), HttpStatus.OK);
    }
}
