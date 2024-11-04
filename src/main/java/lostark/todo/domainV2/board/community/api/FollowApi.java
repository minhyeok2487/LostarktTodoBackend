package lostark.todo.domainV2.board.community.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainV2.board.community.dto.FollowResponse;
import lostark.todo.domainV2.board.community.dto.FollowingUpdateRequest;
import lostark.todo.domainV2.board.community.service.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/follow")
@Api(tags = {"커뮤니티 팔로우 API"})
public class FollowApi {

    private final FollowService service;

    @ApiOperation(value = "내 팔로워", response = FollowResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(service.search(username), HttpStatus.OK);
    }

    @ApiOperation(value = "팔로잉 추가 / 삭제", notes = "데이터 있으면 삭제, 없으면 추가")
    @PostMapping()
    public ResponseEntity<?> update(@AuthenticationPrincipal String username,
                                       @RequestBody FollowingUpdateRequest request) {
        service.update(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
