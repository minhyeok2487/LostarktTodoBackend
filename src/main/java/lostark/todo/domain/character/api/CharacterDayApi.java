package lostark.todo.domain.character.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.dto.UpdateDayCheckRequest;
import lostark.todo.domain.character.dto.UpdateDayGaugeRequest;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.global.friendPermisson.FriendPermissionType;
import lostark.todo.global.friendPermisson.UpdateCharacterMethod;
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
    private final UpdateCharacterMethod updateCharacterMethod;

    @ApiOperation(value = "캐릭터 일일컨텐츠 체크 업데이트", response = CharacterResponse.class)
    @PostMapping("/check")
    public ResponseEntity<?> updateDayCheck(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername,
                                            @RequestBody UpdateDayCheckRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.CHECK_DAY_TODO);
        characterService.updateDayCheck(updateCharacter, request);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 휴식게이지 업데이트",
            response = CharacterResponse.class)
    @PostMapping("/gauge")
    public ResponseEntity<?> updateDayGauge(@AuthenticationPrincipal String username,
                                            @RequestParam(required = false) String friendUsername,
                                            @RequestBody UpdateDayGaugeRequest request) {
        Character updateCharacter = updateCharacterMethod.getUpdateCharacter(username, friendUsername,
                request.getCharacterId(), FriendPermissionType.UPDATE_GAUGE);
        characterService.updateDayGauge(updateCharacter, request);
        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }
}
