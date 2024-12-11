package lostark.todo.domain.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.dto.*;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.util.content.entity.WeekContent;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.util.content.service.ContentService;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.global.friendPermisson.FriendPermissionType;
import lostark.todo.global.friendPermisson.UpdateCharacterMethod;
import lostark.todo.domain.friend.service.FriendsService;
import lostark.todo.domain.character.service.TodoServiceV2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    private final UpdateCharacterMethod updateCharacterMethod;

    // TODO 추후 정리
    @ApiOperation(value = "캐릭터 레이드 추가/제거", response = CharacterResponse.class)
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

    @ApiOperation(value = "캐릭터 주간 레이드 check 수정", response = CharacterResponse.class)
    @PostMapping("/raid/check")
    public ResponseEntity<?> updateWeekRaidCheck(@AuthenticationPrincipal String username,
                                                 @RequestParam(required = false) String friendUsername,
                                                 @RequestBody UpdateWeekRaidCheckRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.CHECK_RAID);
        todoServiceV2.updateWeekRaidCheck(updateCharacter, request);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 레이드 message 수정 (1관문에 저장됨)",
            response = CharacterResponse.class)
    @PostMapping("/raid/message")
    public ResponseEntity<?> updateWeekRaidMessage(@AuthenticationPrincipal String username,
                                                   @RequestParam(required = false) String friendUsername,
                                                   @RequestBody UpdateWeekRaidMessageRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_RAID);
        todoServiceV2.updateWeekMessage(updateCharacter, request);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 레이드 순서 변경", response = CharacterResponse.class)
    @PostMapping("/raid/sort")
    public ResponseEntity<?> updateWeekRaidSort(@AuthenticationPrincipal String username,
                                                @RequestParam(required = false) String friendUsername,
                                                @RequestBody UpdateWeekRaidSortRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_RAID);
        todoServiceV2.updateWeekRaidSort(updateCharacter, request.getSortRequestList());
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 에포나 체크", response = CharacterResponse.class)
    @PostMapping("/epona")
    public ResponseEntity<?> updateWeekEpona(@AuthenticationPrincipal String username,
                                             @RequestParam(required = false) String friendUsername,
                                             @RequestBody UpdateWeekEponaRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.CHECK_WEEK_TODO);
        characterService.updateWeekEpona(updateCharacter, request);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 실마엘 교환 체크", response = CharacterResponse.class)
    @PostMapping("/silmael")
    public ResponseEntity<?> updateWeekSilmael(@AuthenticationPrincipal String username,
                                               @RequestParam(required = false) String friendUsername,
                                               @RequestBody BaseCharacterRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.CHECK_WEEK_TODO);
        characterService.updateWeekSilmael(updateCharacter);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 큐브 티켓 업데이트", response = CharacterResponse.class)
    @PostMapping("/cube")
    public ResponseEntity<?> updateWeekCubeTicket(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername,
                                            @RequestBody UpdateWeekCubeRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.CHECK_WEEK_TODO);
        characterService.updateCubeTicket(updateCharacter, request.getNum());
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "주간 레이드 컨텐츠 골드 획득/해제")
    @PatchMapping("/raid/gold-check")
    public ResponseEntity<?> updateRaidGoldCheck(@AuthenticationPrincipal String username,
                                                 @RequestParam(required = false) String friendUsername,
                                                 @RequestBody UpdateRaidGoldCheckRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_RAID);

        // 골드 체크 업데이트
        characterService.updateRaidGoldCheck(updateCharacter, request.getWeekCategory(), request.isUpdateValue());

        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 레이드 골드 체크 방식 업데이트", response = CharacterDto.class)
    @PatchMapping("/gold-check-version")
    public ResponseEntity<?> updateDayTodoCheck(@AuthenticationPrincipal String username,
                                                @RequestParam(required = false) String friendUsername,
                                                @RequestBody @Valid BaseCharacterRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_SETTING);

        characterService.updateGoldCheckVersion(updateCharacter);

        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }
}
