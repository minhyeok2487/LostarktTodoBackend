package lostark.todo.domain.friend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.friendsDto.UpdateFriendSettingRequest;
import lostark.todo.domain.friend.entity.FriendSettings;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.friend.dto.FriendFindCharacterResponse;
import lostark.todo.domain.friend.dto.FriendRequest;
import lostark.todo.domain.friend.dto.UpdateFriendRequest;
import lostark.todo.domain.friend.enums.FriendRequestCategory;
import lostark.todo.domain.friend.service.FriendsService;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.notification.service.NotificationService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
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


    @ApiOperation(value = "깐부 요청")
    @PostMapping("")
    public ResponseEntity<?> friendsRequest(@AuthenticationPrincipal String username,
                                            @RequestBody FriendRequest request) {
        Member toMember = memberService.get(username);
        if (toMember.getCharacters().isEmpty()) {
            throw new ConditionNotMetException("등록된 캐릭터가 존재하지 않아 깐부 요청이 불가능합니다.");
        }
        Member fromMember = memberService.get(request.getFriendUsername());

        friendsService.addFriendsRequest(toMember, fromMember);

        // 보낸 사랑 알림, 받는 사람 알림
        notificationService.saveAddFriendRequest(toMember, fromMember);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 요청 수락/거부/삭제")
    @PostMapping("/request")
    public ResponseEntity<?> updateFriendsRequest(@AuthenticationPrincipal String username,
                                                  @RequestBody UpdateFriendRequest request) {
        Member toMember = memberService.get(username);
        Member fromMember = memberService.get(request.getFriendUsername());

        friendsService.updateFriendsRequestV2(toMember, fromMember, request.getCategory());
        if (request.getCategory().equals(FriendRequestCategory.OK)) {
            notificationService.saveUpdateFriendRequestOk(toMember, fromMember);
        } else if (request.getCategory().equals(FriendRequestCategory.REJECT)) {
            notificationService.saveUpdateFriendRequestReject(toMember, fromMember);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 권한 수정", response = FriendSettings.class)
    @PatchMapping("/settings")
    public ResponseEntity<?> UpdateFriendSetting(@AuthenticationPrincipal String username,
                                              @RequestBody UpdateFriendSettingRequest request) {
        FriendSettings friendSettings = friendsService.updateSetting(request);
        return new ResponseEntity<>(friendSettings, HttpStatus.OK);
    }
}
