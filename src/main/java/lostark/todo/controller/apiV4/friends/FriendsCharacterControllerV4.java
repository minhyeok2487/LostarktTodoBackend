package lostark.todo.controller.apiV4.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.controller.dtoV2.character.UpdateMemoRequest;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.friend.service.FriendsService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static lostark.todo.global.exhandler.ErrorMessageConstants.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/friends/character")
@Api(tags = {"깐부 단일 캐릭터 API"})
public class FriendsCharacterControllerV4 {

    private final CharacterService characterService;
    private final FriendsService friendsService;

    //TODO 추후삭제
    @ApiOperation(value = "깐부 골드 획득 캐릭터 지정/해제", response = CharacterResponse.class)
    @PatchMapping("/{friendUsername}/gold-character")
    public ResponseEntity<CharacterResponse> updateGoldCharacter(@AuthenticationPrincipal String username,
                                                                 @RequestBody CharacterDefaultDto characterDefaultDto,
                                                                 @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new ConditionNotMetException(FRIEND_PERMISSION_DENIED);
        }

        Character resultCharacter = characterService.updateGoldCharacter(characterDefaultDto, friendUsername);
        return new ResponseEntity<>(new CharacterResponse().toDto(resultCharacter), HttpStatus.OK);
    }

    //TODO 추후삭제
    @ApiOperation(value = "캐릭터 메모 업데이트", notes = "기본 값 null / 길이 제한 100 / null 혹은 빈 칸으로 입력시 null로 저장")
    @PostMapping("/{friendUsername}/memo")
    public ResponseEntity<?> updateMemo(@AuthenticationPrincipal String username,
                                        @RequestBody @Valid UpdateMemoRequest updateMemoRequest,
                                        @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new ConditionNotMetException(FRIEND_PERMISSION_DENIED);
        }

        Character updateCharacter = characterService.updateMemo(friendUsername, updateMemoRequest);
        return new ResponseEntity<>(new CharacterResponse().toDto(updateCharacter), HttpStatus.OK);
    }
}
