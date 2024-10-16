package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.friendsDto.FindCharacterWithFriendsDto;
import lostark.todo.controller.dto.friendsDto.FriendSettingRequestDto;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domain.friends.FriendSettings;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.character.service.CharacterService;
import lostark.todo.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v2/friends")
@Api(tags = {"친구, 깐부 API"})
public class FriendsApiController {

    private final CharacterService characterService;
    private final MemberService memberService;
    private final FriendsService friendsService;
    private final NotificationService notificationService;

    @ApiOperation(value = "캐릭터 검색")
    @GetMapping("/character/{characterName}")
    public ResponseEntity getCharacterWithFriend(@AuthenticationPrincipal String username,
                                                 @PathVariable String characterName) {
        if(characterName.isEmpty()) {
            throw new IllegalArgumentException("캐릭터명을 입력하여주십시오.");
        }
        Member toMember = memberService.get(username);
        List<Character> characterList = characterService.findCharacter(characterName);
        if(!characterList.isEmpty()) {
            List<FindCharacterWithFriendsDto> dtoList = new ArrayList<>();
            for (Character character : characterList) {
                if(toMember != character.getMember()) { //본인 제외
                    Member fromMember = memberService.get(character.getMember().getId());
                    String weAreFriend = friendsService.isFriend(toMember,fromMember);
                    FindCharacterWithFriendsDto dto = FindCharacterWithFriendsDto.builder()
                            .id(fromMember.getId())
                            .username(fromMember.getUsername())
                            .characterName(characterName)
                            .characterListSize(fromMember.getCharacters().size())
                            .areWeFriend(weAreFriend)
                            .build();
                    dtoList.add(dto);
                }
            }
            return new ResponseEntity<>(dtoList, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException(characterName + "은(는) 등록되지 않은 캐릭터명입니다.");
        }
    }

    @ApiOperation(value = "친구 요청")
    @PostMapping("/{fromUser}")
    public ResponseEntity addFriendsRequest(@AuthenticationPrincipal String username,
                                            @PathVariable String fromUser) {
        Member toMember = memberService.get(username);
        if (toMember.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 존재하지 않아 깐부 요청이 불가능합니다.");
        }
        Member fromMember = memberService.get(fromUser);

        friendsService.addFriendsRequest(toMember, fromMember);

        // 보낸 사랑 알림, 받는 사람 알림
        notificationService.saveAddFriendRequest(toMember, fromMember);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "친구 요청 수락/거부/삭제")
    @PatchMapping("/{fromUser}/{category}")
    public ResponseEntity updateFriendsRequest(@AuthenticationPrincipal String username,
                                               @PathVariable("fromUser") String fromUser,
                                               @PathVariable("category") String category) {
        Member toMember = memberService.get(username);
        Member fromMember = memberService.get(fromUser);

        friendsService.updateFriendsRequest(toMember, fromMember, category);
        if (category.equals("ok")) {
            notificationService.saveUpdateFriendRequestOk(toMember, fromMember);
        } else if (category.equals("reject")) {
            notificationService.saveUpdateFriendRequestReject(toMember, fromMember);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 권한 수정")
    @PatchMapping("/settings")
    public ResponseEntity updateSettings(@AuthenticationPrincipal String username,
                                         @RequestBody FriendSettingRequestDto friendSettingRequestDto) {

        FriendSettings friendSettings = friendsService.updateSetting(friendSettingRequestDto.getId(),
                friendSettingRequestDto.getName(), friendSettingRequestDto.isValue());

        return new ResponseEntity(friendSettings, HttpStatus.OK);
    }
}
