package lostark.todo.domainV2.friend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainV2.friend.dto.FriendFindCharacterResponse;
import lostark.todo.service.FriendsService;
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

    @ApiOperation(value = "캐릭터 검색", response = FriendFindCharacterResponse.class)
    @GetMapping("/character/{characterName}")
    public ResponseEntity<?> findCharacter(@AuthenticationPrincipal String username,
                                           @PathVariable String characterName) {
        return new ResponseEntity<>(friendsService.findCharacter(username, characterName), HttpStatus.OK);
    }

}
