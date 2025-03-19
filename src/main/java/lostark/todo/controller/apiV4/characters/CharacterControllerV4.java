package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/character")
@Api(tags = {"단일 캐릭터 API"})
public class CharacterControllerV4 {

    private final CharacterService characterService;

    //TODO 추후삭제
    @ApiOperation(value = "골드 획득 캐릭터 지정/해제", response = CharacterResponse.class)
    @PatchMapping("/gold-character")
    public ResponseEntity<CharacterResponse> updateGoldCharacter(@AuthenticationPrincipal String username,
                                                            @RequestBody CharacterDefaultDto characterDefaultDto) {
        Character resultCharacter = characterService.updateGoldCharacter(characterDefaultDto, username);
        return new ResponseEntity<>(new CharacterResponse().toDto(resultCharacter), HttpStatus.OK);
    }

    //TODO 추후삭제
    @ApiOperation(value = "등록 캐릭터 단일 삭제")
    @DeleteMapping("/{characterId}")
    public ResponseEntity<?> deleteCharacter(@AuthenticationPrincipal String username, @PathVariable Long characterId) {
        characterService.delete(characterId, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
