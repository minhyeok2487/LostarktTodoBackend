package lostark.todo.domainV2.friend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.friend.dto.FriendFindCharacterResponse;
import lostark.todo.domainV2.friend.dto.FriendRequest;
import lostark.todo.service.FriendsService;
import lostark.todo.service.MemberService;
import lostark.todo.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/friend")
@Api(tags = {"깐부(친구) API"})
public class FriendApi {

    private final FriendsService friendsService;
    private final MemberService memberService;
    private final NotificationService notificationService;

    @ApiOperation(value = "캐릭터 검색", response = FriendFindCharacterResponse.class)
    @GetMapping("/character/{characterName}")
    public ResponseEntity<?> findCharacter(@AuthenticationPrincipal String username,
                                           @PathVariable String characterName) {
        return new ResponseEntity<>(friendsService.findCharacter(username, characterName), HttpStatus.OK);
    }


    @ApiOperation(value = "친구 요청")
    @PostMapping("")
    public ResponseEntity<?> friendsRequest(@AuthenticationPrincipal String username,
                                            @RequestBody FriendRequest request) {
        Member toMember = memberService.get(username);
        if (toMember.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 존재하지 않아 깐부 요청이 불가능합니다.");
        }
        Member fromMember = memberService.get(request.getFriendUsername());

        friendsService.addFriendsRequest(toMember, fromMember);

        // 보낸 사랑 알림, 받는 사람 알림
        notificationService.saveAddFriendRequest(toMember, fromMember);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}