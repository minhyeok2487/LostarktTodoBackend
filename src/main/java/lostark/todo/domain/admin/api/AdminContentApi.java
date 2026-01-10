package lostark.todo.domain.admin.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.admin.dto.AddContentRequest;
import lostark.todo.domain.content.entity.Content;
import lostark.todo.domain.content.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/api/v1/contents")
@Api(tags = {"어드민 - 컨텐츠 관리"})
public class AdminContentApi {

    private final ContentService contentService;

    @ApiOperation(value = "컨텐츠 목록 조회")
    @GetMapping
    public ResponseEntity<?> getContentList(@RequestParam(required = false) String contentType) {
        return new ResponseEntity<>(contentService.getContentListForAdmin(contentType), HttpStatus.OK);
    }

    @ApiOperation(value = "컨텐츠 상세 조회")
    @GetMapping("/{contentId}")
    public ResponseEntity<?> getContentDetail(@PathVariable Long contentId) {
        return new ResponseEntity<>(contentService.getByIdForAdmin(contentId), HttpStatus.OK);
    }

    @ApiOperation(value = "컨텐츠 추가")
    @PostMapping
    public ResponseEntity<?> addContent(@RequestBody @Valid AddContentRequest request) {
        return new ResponseEntity<>(contentService.addContent(request), HttpStatus.OK);
    }

    @ApiOperation(value = "컨텐츠 수정")
    @PutMapping("/{contentId}")
    public ResponseEntity<?> updateContent(@PathVariable Long contentId,
                                           @RequestBody @Valid AddContentRequest request) {
        Content content = contentService.updateContent(contentId, request);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @ApiOperation(value = "컨텐츠 삭제")
    @DeleteMapping("/{contentId}")
    public ResponseEntity<?> deleteContent(@PathVariable Long contentId) {
        contentService.deleteContent(contentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
