package lostark.todo.domain.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterSortRequest;
import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.domain.character.dto.DeletedCharacterResponse;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.friend.service.FriendsService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static lostark.todo.global.exhandler.ErrorMessageConstants.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/character-list")
@Api(tags = {"캐릭터 리스트 API"})
public class CharacterListApi {

    private final CharacterService characterService;
    private final FriendsService friendsService;

    @ApiOperation(value = "캐릭터 + 숙제 정보 조회 API",
            response = CharacterResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(characterService.getCharacterList(username), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 리스트 순서변경 저장", response = CharacterResponse.class)
    @PatchMapping("/sorting")
    public ResponseEntity<?> updateSort(@AuthenticationPrincipal String username,
                                        @RequestParam(required = false) String friendUsername,
                                        @RequestBody @Valid List<CharacterSortRequest> characterSortRequestList) {
        if (friendUsername == null) {
            return new ResponseEntity<>(characterService.editSort(username, characterSortRequestList), HttpStatus.OK);
        } else {
            Friends friend = friendsService.findByFriendUsername(friendUsername, username);
            if (!friend.getFriendSettings().isSetting()) {
                throw new ConditionNotMetException(FRIEND_PERMISSION_DENIED);
            } else {
                return new ResponseEntity<>(characterService.editSort(friendUsername, characterSortRequestList), HttpStatus.OK);
            }
        }
    }

    @ApiOperation(value = "삭제된 캐릭터 리스트 조회", response = DeletedCharacterResponse.class)
    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedCharacter(@AuthenticationPrincipal String username,
                                                 @RequestParam(required = false) String friendUsername) {
        if (friendUsername == null) {
            return new ResponseEntity<>(characterService.getDeletedCharacter(username), HttpStatus.OK);
        } else {
            Friends friend = friendsService.findByFriendUsername(friendUsername, username);
            if (!friend.getFriendSettings().isSetting()) {
                throw new ConditionNotMetException(FRIEND_PERMISSION_DENIED);
            } else {
                return new ResponseEntity<>(characterService.getDeletedCharacter(friendUsername), HttpStatus.OK);
            }
        }
    }
}
