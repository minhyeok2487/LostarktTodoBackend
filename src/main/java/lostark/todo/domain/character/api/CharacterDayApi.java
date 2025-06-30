package lostark.todo.domain.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterResponse;
import lostark.todo.domain.character.dto.UpdateDayCheckAllCharactersRequest;
import lostark.todo.domain.character.dto.UpdateDayCheckAllRequest;
import lostark.todo.domain.character.dto.UpdateDayCheckRequest;
import lostark.todo.domain.character.dto.UpdateDayGaugeRequest;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.global.friendPermisson.FriendPermissionType;
import lostark.todo.global.friendPermisson.CharacterMemberQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/character/day")
@Api(tags = {"캐릭터 일일 숙제 Api"})
public class CharacterDayApi {

    private final CharacterService characterService;
    private final CharacterMemberQueryService characterMemberQueryService;

    @ApiOperation(value = "캐릭터 일일컨텐츠 체크 업데이트", response = CharacterResponse.class)
    @PostMapping("/check")
    public ResponseEntity<?> updateDayCheck(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername,
                                            @RequestBody UpdateDayCheckRequest request) {
        Character updateCharacter = characterMemberQueryService.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.CHECK_DAY_TODO);
        return new ResponseEntity<>(characterService.updateDayCheck(updateCharacter, request), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 휴식게이지 업데이트",
            response = CharacterResponse.class)
    @PostMapping("/gauge")
    public ResponseEntity<?> updateDayGauge(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername,
                                            @RequestBody UpdateDayGaugeRequest request) {
        // 1. 캐릭터 호출 (깐부면 권한 체크)
        Character updateCharacter = characterMemberQueryService.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_GAUGE);

        // 2. 값 검증
        characterService.validateUpdateDayGauge(request);

        // 3. 휴식 게이지 업데이트
        characterService.updateDayGauge(updateCharacter, request);
        return new ResponseEntity<>(new CharacterResponse().toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "단일 캐릭터 일일컨텐츠 전체 체크 업데이트", response = CharacterResponse.class)
    @PostMapping("/check/all")
    public ResponseEntity<?> updateDayCheck(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername,
                                            @RequestBody UpdateDayCheckAllRequest request) {
        Character updateCharacter = characterMemberQueryService.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.CHECK_DAY_TODO);
        return new ResponseEntity<>(characterService.updateDayCheckAll(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "전체 캐릭터 일일컨텐츠 전체 체크(출력된 것만)", response = CharacterResponse.class)
    @PostMapping("/check/all-characters")
    // TODO 추후 깐부 작업
    public ResponseEntity<?> updateDayCheckAllCharacters(@AuthenticationPrincipal String username,
                                                         @RequestParam(required = false) String friendUsername,
                                                         @RequestBody UpdateDayCheckAllCharactersRequest request) {
        return new ResponseEntity<>(characterService.updateDayCheckAllCharacters(username, request.getServerName()), HttpStatus.OK);
    }
}
