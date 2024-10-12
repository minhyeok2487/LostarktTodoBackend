package lostark.todo.domainV2.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.SettingRequestDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.service.CharacterService;
import lostark.todo.service.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/character")
@Api(tags = {"단일 캐릭터 API"})
public class CharacterApi {

    private final CharacterService characterService;
    private final FriendsService friendsService;

    @ApiOperation(value = "캐릭터 출력 내용 수정", response = CharacterResponse.class)
    @PatchMapping("/settings")
    public ResponseEntity<?> updateSettings(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername,
                                            @RequestBody SettingRequestDto settingRequestDto) {
        Character updateCharacter;
        if (friendUsername == null) {
            updateCharacter = characterService.updateSetting(username, settingRequestDto);
        } else {
            Friends friend = friendsService.findByFriendUsername(friendUsername, username);
            if (!friend.getFriendSettings().isSetting()) {
                throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
            } else {
                updateCharacter = characterService.updateSetting(friendUsername, settingRequestDto);
            }
        }
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }
}
