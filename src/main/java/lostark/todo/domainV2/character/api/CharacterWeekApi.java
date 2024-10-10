package lostark.todo.domainV2.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domainV2.character.dto.UpdateWeekRaidCheckRequest;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domainV2.character.dto.UpdateWeekRaidRequest;
import lostark.todo.domainV2.util.content.service.ContentService;
import lostark.todo.domainV2.character.service.CharacterService;
import lostark.todo.service.FriendsService;
import lostark.todo.service.TodoServiceV2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/character/week")
@Api(tags = {"캐릭터 주간 숙제 API"})
public class CharacterWeekApi {

    private final CharacterService characterService;
    private final FriendsService friendsService;
    private final ContentService contentService;
    private final TodoServiceV2 todoServiceV2;

    // TODO 추후 정리
    @ApiOperation(value = "캐릭터 레이드 추가/제거")
    @PostMapping("/raid")
    public ResponseEntity<?> updateWeekRaid(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername,
                                            @RequestBody @Valid UpdateWeekRaidRequest request) {

        Character character;

        // 친구인지 본인인지에 따라 캐릭터 정보 가져오기
        if (friendUsername == null) {
            character = characterService.get(request.getCharacterId(), username);
        } else {
            Friends friend = friendsService.findByFriendUsername(friendUsername, username);
            if (!friend.getFriendSettings().isSetting()) {
                throw new IllegalArgumentException("권한이 없습니다.");
            }
            character = friend.getMember().getCharacters().stream()
                    .filter(c -> c.getId() == request.getCharacterId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐릭터 입니다."));
        }

        // 주간 콘텐츠 목록 가져오기
        List<WeekContent> weekContentList = contentService.findAllByIdWeekContent(request.getWeekContentIdList())
                .stream()
                .map(content -> (WeekContent) content)
                .toList();

        // 주간 레이드 업데이트
        if (weekContentList.size() == 1) {
            todoServiceV2.updateWeekRaid(character, weekContentList.get(0));
        } else {
            todoServiceV2.updateWeekRaidAll(character, weekContentList);
        }

        return new ResponseEntity<>(CharacterResponse.toDto(character), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 레이드 check 수정")
    @PatchMapping("/raid/check")
    public ResponseEntity<?> updateWeekRaidCheck(@AuthenticationPrincipal String username,
                                                 @RequestParam(required = false) String friendUsername,
                                                 @RequestBody UpdateWeekRaidCheckRequest request) {
        Character updateCharacter;
        if (friendUsername == null) {
            updateCharacter = characterService.get(request.getCharacterId(), username);
        } else {
            Friends friend = friendsService.findByFriendUsername(friendUsername, username);
            if (!friend.getFriendSettings().isUpdateRaid()) {
                throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
            } else {
                updateCharacter = characterService.get(request.getCharacterId(), friendUsername);
            }
        }
        todoServiceV2.updateWeekRaidCheck(updateCharacter, request);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);

    }


}
