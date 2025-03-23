package lostark.todo.controller.apiV4.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.firend.FriendsResponse;
import lostark.todo.controller.dtoV2.firend.UpdateSortRequest;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.friend.service.FriendsService;
import lostark.todo.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/friends")
@Api(tags = {"깐부 API"})
public class FriendsControllerV4 {

    private final FriendsService friendsService;
    private final MemberService memberService;

    @ApiOperation(value = "깐부 리스트 조회",
            response = FriendsResponse.class)
    @GetMapping()
    // TODO 추후삭제
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(friendsService.getFriendListV2(memberService.get(username).getId()), HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 삭제")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String username, @PathVariable long friendId) {
        Member member = memberService.get(username);
        friendsService.deleteById(member, friendId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 순서 변경")
    @PutMapping("/sort")
    public ResponseEntity<?> updateSort(@AuthenticationPrincipal String username,
                                        @RequestBody UpdateSortRequest updateSortRequest) {
        Member member = memberService.get(username);
        friendsService.updateSort(member, updateSortRequest.getFriendIdList());

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
