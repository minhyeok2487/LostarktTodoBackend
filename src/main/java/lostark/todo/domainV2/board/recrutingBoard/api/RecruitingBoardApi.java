package lostark.todo.domainV2.board.recrutingBoard.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.boardsDto.ImageUrlDto;
import lostark.todo.domainV2.board.recrutingBoard.dto.*;
import lostark.todo.domainV2.board.recrutingBoard.service.RecruitingBoardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/recruiting-board")
@Api(tags = {"모집 게시판 API"})
public class RecruitingBoardApi {

    private final RecruitingBoardService recruitingBoardService;

    @ApiOperation(value = "모집 게시글 작성 API", response = CreateRecruitingBoardResponse.class)
    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestBody CreateRecruitingBoardRequest request) {
        return new ResponseEntity<>(recruitingBoardService.create(username, request), HttpStatus.OK);
    }

    @ApiOperation(value = "모집 게시글 리스트 조회", response = SearchRecruitingBoardResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(SearchRecruitingBoardRequest request,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page-1, limit);
        return new ResponseEntity<>(recruitingBoardService.search(request, pageRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "모집 게시글 단건 조회", response = GetRecruitingBoardResponse.class)
    @GetMapping("/{recruitingBoardId}")
    public ResponseEntity<?> get(GetRecruitingBoardRequest request,
                                 @PathVariable Long recruitingBoardId) {
        return new ResponseEntity<>(recruitingBoardService.get(recruitingBoardId, request.getToken()), HttpStatus.OK);
    }

    @ApiOperation(value = "모집 게시글 수정 API")
    @PutMapping("/{recruitingBoardId}")
    public ResponseEntity<?> update(@AuthenticationPrincipal String username,
                                    @RequestBody UpdateRecruitingBoardRequest request,
                                    @PathVariable Long recruitingBoardId) {
        recruitingBoardService.update(username, request, recruitingBoardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "모집 게시글 삭제 API")
    @DeleteMapping("/{recruitingBoardId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String username,
                                    @PathVariable Long recruitingBoardId) {
        recruitingBoardService.delete(username, recruitingBoardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "이미지 업로드", response = ImageUrlDto.class)
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@AuthenticationPrincipal String username, @RequestPart("image") MultipartFile image) {
        return new ResponseEntity<>(recruitingBoardService.uploadImage(username, image), HttpStatus.OK);
    }
}
