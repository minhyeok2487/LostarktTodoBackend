package lostark.todo.domain.board.community.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.board.community.dto.*;
import lostark.todo.domain.board.community.entity.CommunityCategory;
import lostark.todo.domain.board.community.service.CommunityService;
import lostark.todo.global.dto.ImageResponseV2;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/community")
@Api(tags = {"커뮤니티 API"})
public class CommunityApi {

    private final CommunityService service;

    @ApiOperation(value = "커뮤니티 카테고리 목록")
    @GetMapping("/category")
    public ResponseEntity<?> getCommunityCategory() {
        return new ResponseEntity<>(Arrays.stream(CommunityCategory.values())
                .map(CommunityCategory::getCategoryName)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 불러오기 (커서 기반)", response = CommunitySearchResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username,
                                    @Valid CommunitySearchParams params,
                                    @RequestParam(required = false, defaultValue = "20") int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return new ResponseEntity<>(service.search(username, params, pageRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 상세 불러오기", response = CommunityGetResponse.class)
    @GetMapping("/{communityId}")
    public ResponseEntity<?> get(@AuthenticationPrincipal String username,
                                 @PathVariable Long communityId) {
        return new ResponseEntity<>(service.get(username, communityId), HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 저장")
    @PostMapping()
    public ResponseEntity<?> save(@AuthenticationPrincipal String username,
                                  @RequestBody @Valid CommunitySaveRequest request) {
        service.save(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "이미지 업로드", response = ImageResponseV2.class)
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@AuthenticationPrincipal String username,
                                         @RequestPart("image") MultipartFile image) {
        return new ResponseEntity<>(service.uploadImage(username, image), HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 수정", notes = "15분이내 게시글만 수정 가능, 사진 수정은 불가능")
    @PatchMapping("")
    public ResponseEntity<?> update(@AuthenticationPrincipal String username,
                                    @RequestBody @Valid CommunityUpdateRequest request) {
        service.update(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 삭제")
    @DeleteMapping("/{communityId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String username,
                                    @PathVariable long communityId) {
        service.delete(username, communityId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "좋아요")
    @PostMapping("/like/{communityId}")
    public ResponseEntity<?> updateLike(@AuthenticationPrincipal String username,
                                        @PathVariable long communityId) {
        service.updateLike(username, communityId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
