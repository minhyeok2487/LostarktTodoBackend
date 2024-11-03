package lostark.todo.domainV2.board.community.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainV2.board.community.service.FollowSerivce;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/follow")
@Api(tags = {"커뮤니티 팔로우 API"})
public class FollowApi {

    private final FollowSerivce service;

    @ApiOperation(value = "팔로우 목록 호출")
    @GetMapping()
    public ResponseEntity<?> search() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
