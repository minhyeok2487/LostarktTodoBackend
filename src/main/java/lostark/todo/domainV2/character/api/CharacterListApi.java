package lostark.todo.domainV2.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domain.friends.Friends;
import lostark.todo.service.CharacterService;
import lostark.todo.service.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/character-list")
@Api(tags = {"회원 캐릭터 리스트 관련 API"})
public class CharacterListApi {

    private final CharacterService characterService;
    private final FriendsService friendsService;

    @ApiOperation(value = "회원 캐릭터 리스트 업데이트",
            notes="전투 레벨, 아이템 레벨, 이미지url 업데이트 \n" +
                    "캐릭터 아이템 레벨이 달라지면 예상 수익골드 다시 계산 \n" +
                    "캐릭터 추가 및 삭제 ",
            response = CharacterDto.class)
    @PutMapping("")
    public ResponseEntity<?> updateCharacterList(@AuthenticationPrincipal String username,
                                                 @RequestParam(required = false) String friendUsername) {
        if (friendUsername == null) {
            characterService.updateCharacterList(username);
        } else {
            Friends friend = friendsService.findByFriendUsername(friendUsername, username);
            if (!friend.getFriendSettings().isSetting()) {
                throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
            } else {
                characterService.updateCharacterList(friendUsername);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}