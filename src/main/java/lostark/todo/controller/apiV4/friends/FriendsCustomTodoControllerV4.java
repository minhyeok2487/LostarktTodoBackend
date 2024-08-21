package lostark.todo.controller.apiV4.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.character.CheckCustomTodoRequest;
import lostark.todo.controller.dtoV2.character.CreateCustomTodoRequest;
import lostark.todo.controller.dtoV2.character.CustomTodoResponse;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.customTodo.CustomTodo;
import lostark.todo.domain.friends.Friends;
import lostark.todo.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/friends/custom")
@Api(tags = {"깐부 커스텀 숙제 API"})
public class FriendsCustomTodoControllerV4 {

    private final MemberService memberService;
    private final CharacterService characterService;
    private final CustomTodoService customTodoService;
    private final FriendsService friendsService;

    @ApiOperation(value = "깐부 커스텀 숙제 조회")
    @GetMapping("/{friendUsername}")
    public ResponseEntity<?> search(@AuthenticationPrincipal String username, @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        List<CustomTodo> search = customTodoService.search(friend.getMember().getUsername());
        List<CustomTodoResponse> response = search.stream().map(CustomTodoResponse::new).toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 추가")
    @PostMapping("/{friendUsername}")
    public ResponseEntity<?> create(@AuthenticationPrincipal String username,
                                    @RequestBody CreateCustomTodoRequest request, @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }

        Character character = characterService.get(request.getCharacterId(), friendUsername);
        customTodoService.create(character, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 체크")
    @PostMapping("/{friendUsername}/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal String username,
                                   @RequestBody CheckCustomTodoRequest request, @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        Character character = characterService.get(request.getCharacterId(), friendUsername);
        customTodoService.friendCheck(character, request, friend);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "커스텀 숙제 삭제")
    @DeleteMapping("/{friendUsername}/{customTodoId}")
    public ResponseEntity<?> remove(@AuthenticationPrincipal String username, @PathVariable Long customTodoId,
                                    @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }

        List<Long> characterIdList = memberService.get(friendUsername).getCharacters().stream().map(Character::getId).toList();
        customTodoService.remove(characterIdList, customTodoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
