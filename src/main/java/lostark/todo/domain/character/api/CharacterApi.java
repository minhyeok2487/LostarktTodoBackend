package lostark.todo.domain.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.SettingRequestDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.controller.dtoV2.character.UpdateMemoRequest;
import lostark.todo.domain.character.dto.BaseCharacterRequest;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.friend.service.FriendsService;
import lostark.todo.global.friendPermisson.FriendPermissionType;
import lostark.todo.global.friendPermisson.UpdateCharacterMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static lostark.todo.global.exhandler.ErrorMessageConstants.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/character")
@Api(tags = {"단일 캐릭터 API"})
public class CharacterApi {

    private final CharacterService characterService;
    private final FriendsService friendsService;
    private final UpdateCharacterMethod updateCharacterMethod;


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

    @ApiOperation(value = "삭제된 캐릭터 복구")
    @PostMapping("/deleted")
    public ResponseEntity<?> updateDeletedCharacter(@AuthenticationPrincipal String username,
                                                    @RequestParam(required = false) String friendUsername,
                                                    @RequestBody BaseCharacterRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_SETTING);
        characterService.updateDeletedCharacter(updateCharacter);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "골드 획득 캐릭터 지정/해제", response = CharacterResponse.class)
    @PatchMapping("/gold-character")
    public ResponseEntity<CharacterResponse> updateGoldCharacter(@AuthenticationPrincipal String username,
                                                                 @RequestParam(required = false) String friendUsername,
                                                                 @RequestBody BaseCharacterRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_SETTING);
        characterService.updateGoldCharacter(updateCharacter);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 메모 업데이트", notes = "기본 값 null / 길이 제한 100 / null 혹은 빈 칸으로 입력시 null로 저장", response = CharacterResponse.class)
    @PostMapping("/memo")
    public ResponseEntity<?> updateMemo(@AuthenticationPrincipal String username,
                                        @RequestParam(required = false) String friendUsername,
                                        @RequestBody @Valid UpdateMemoRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_SETTING);
        characterService.updateMemo(updateCharacter, request.getMemo());
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "등록 캐릭터 단일 삭제")
    @DeleteMapping("/{characterId}")
    public ResponseEntity<?> deleteCharacter(@AuthenticationPrincipal String username,
                                             @RequestParam(required = false) String friendUsername,
                                             @PathVariable Long characterId) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                characterId, FriendPermissionType.UPDATE_SETTING);
        characterService.delete(updateCharacter);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
