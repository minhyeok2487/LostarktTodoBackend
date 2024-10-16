package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.noticesDto.NoticesDto;
import lostark.todo.domain.notices.Notices;
import lostark.todo.global.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.service.NoticesService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/notices")
@Api(tags = {"로스트아크 공지사항"})
public class NoticeController {

    private final NoticesService noticesService;

    @ApiOperation(value = "로스트아크 공지사항 page, size 크기로 가져오기",
            notes = "sort : 작성일 최근순, page : 1부터 시작",
            response = NoticesDto.class)
    @GetMapping("")
    public ResponseEntity<?> findAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "5") int size) {
        if (page < 1) {
            throw new CustomIllegalArgumentException("로스트아크 공지사항 가져오기 에러", "page 입력 값은 1보다 커야 합니다.", null);
        }
        if (size < 1) {
            throw new CustomIllegalArgumentException("로스트아크 공지사항 가져오기 에러", "size 입력 값은 1보다 커야 합니다.", null);
        }

        Page<Notices> all = noticesService.findAll(page - 1, size);
        int totalPages = all.getTotalPages();

        return new ResponseEntity<>(new NoticesDto(all.getContent(), totalPages, page), HttpStatus.OK);
    }

}
