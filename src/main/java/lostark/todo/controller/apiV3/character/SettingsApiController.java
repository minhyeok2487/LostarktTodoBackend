package lostark.todo.controller.apiV3.character;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v3/character/settings")
@Api(tags = {"캐릭터 API - 설정 변경"})
//TODO 추후 삭제
public class SettingsApiController {

    private final CharacterService characterService;

    @ApiOperation(value = "캐릭터 레이드 골드 체크 방식 업데이트", response = CharacterDto.class)
    @PatchMapping("/gold-check-version")
    public ResponseEntity<?> updateDayTodoCheck(@AuthenticationPrincipal String username,
                                                @RequestBody @Valid CharacterDefaultDto characterDefaultDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.get(
                characterDefaultDto.getCharacterId(), characterDefaultDto.getCharacterName(), username);

        Character updateCharacter = characterService.updateGoldCheckVersion(character);

        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }


}
