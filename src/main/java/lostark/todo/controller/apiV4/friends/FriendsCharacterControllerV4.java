package lostark.todo.controller.apiV4.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.todoDto.raid.RaidGoldCheckRequestDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.controller.dtoV2.character.UpdateMemoRequest;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.friend.entity.Friends;
import lostark.todo.domainV2.character.service.CharacterService;
import lostark.todo.domainV2.friend.service.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static lostark.todo.global.exhandler.ErrorMessageConstants.CHARACTER_NOT_FOUND;
import static lostark.todo.global.exhandler.ErrorMessageConstants.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/friends/character")
@Api(tags = {"깐부 단일 캐릭터 API"})
public class FriendsCharacterControllerV4 {

    private final CharacterService characterService;
    private final FriendsService friendsService;

    @ApiOperation(value = "깐부 캐릭터 레이드 골드 체크 방식 업데이트", response = CharacterResponse.class)
    @PatchMapping("/{friendUsername}/gold-check-version")
    public ResponseEntity<?> updateDayTodoCheck(
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

        return new ResponseEntity<>(CharacterResponse.toDto(updatedCharacter), HttpStatus.OK);
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

    @ApiOperation(value = "주간 레이드 컨텐츠 골드 획득/해제")
    @PatchMapping("/{friendUsername}/gold-check")
    public ResponseEntity<?> updateRaidGoldCheck(@AuthenticationPrincipal String username,
                                                 @RequestBody RaidGoldCheckRequestDto requestDto,
                                                 @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }

        Character character = characterService.get(requestDto.getCharacterId(),
                requestDto.getCharacterName(), friendUsername);

        // 골드 체크 업데이트
        characterService.updateRaidGoldCheck(character, requestDto.getWeekCategory(), requestDto.isUpdateValue());

        return new ResponseEntity<>(CharacterResponse.toDto(character), HttpStatus.OK);
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
