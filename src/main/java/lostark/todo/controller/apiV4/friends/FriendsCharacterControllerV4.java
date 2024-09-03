package lostark.todo.controller.apiV4.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.controller.dtoV2.character.UpdateMemoRequest;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.friends.Friends;
import lostark.todo.service.CharacterService;
import lostark.todo.service.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static lostark.todo.constants.ErrorMessages.CHARACTER_NOT_FOUND;
import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/friends/character")
@Api(tags = {"깐부 단일 캐릭터 API"})
public class FriendsCharacterControllerV4 {

    private final CharacterService characterService;
    private final FriendsService friendsService;

    @ApiOperation(value = "깐부 캐릭터 레이드 골드 체크 방식 업데이트", response = CharacterDto.class)
    @PatchMapping("/{friendUsername}/gold-check-version")
    public ResponseEntity<CharacterDto> updateDayTodoCheck(
            @AuthenticationPrincipal String username,
            @RequestBody @Valid CharacterDefaultDto characterDefaultDto,
            @PathVariable String friendUsername) {

        Friends friend = friendsService.findByFriendUsername(friendUsername, username);

        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }

        Character character = friend.getMember().getCharacters().stream()
                .filter(c -> c.getId() == characterDefaultDto.getCharacterId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(CHARACTER_NOT_FOUND));

        Character updatedCharacter = characterService.updateGoldCheckVersion(character);
        CharacterDto characterDto = new CharacterDto().toDtoV2(updatedCharacter);

        return new ResponseEntity<>(characterDto, HttpStatus.OK);
    }


    @ApiOperation(value = "깐부 골드 획득 캐릭터 지정/해제", response = CharacterResponse.class)
    @PatchMapping("/{friendUsername}/gold-character")
    public ResponseEntity<CharacterResponse> updateGoldCharacter(@AuthenticationPrincipal String username,
                                                                 @RequestBody CharacterDefaultDto characterDefaultDto,
                                                                 @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }

        Character resultCharacter = characterService.updateGoldCharacter(characterDefaultDto, friendUsername);
        return new ResponseEntity<>(CharacterResponse.toDto(resultCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 메모 업데이트", notes = "기본 값 null / 길이 제한 100 / null 혹은 빈 칸으로 입력시 null로 저장")
    @PostMapping("/{friendUsername}/memo")
    public ResponseEntity<?> updateMemo(@AuthenticationPrincipal String username,
                                        @RequestBody @Valid UpdateMemoRequest updateMemoRequest,
                                        @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }

        Character updateCharacter = characterService.updateMemo(friendUsername, updateMemoRequest);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }
}
