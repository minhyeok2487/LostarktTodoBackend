package lostark.todo.controller.v2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.v2.dto.characterDto.CharacterResponseDtoV2;
import lostark.todo.domain.character.Character;
import lostark.todo.service.v2.CharacterServiceV2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v2/characters")
@Api(tags = {"캐릭터 REST API"})
public class CharacterApiControllerV2 {

    private final CharacterServiceV2 characterService;

    @ApiOperation(value = "캐릭터 데이터 조회", response = CharacterResponseDtoV2.class)
    @GetMapping("/{characterName}")
    public ResponseEntity findCharacter(@PathVariable String characterName) {
        // 캐릭터 정보 가져옴
        // 일일컨텐츠 이름, 예상 수익 없음
        Character character = characterService.findCharacter(characterName);

        // 리턴 Dto
        CharacterResponseDtoV2 result = CharacterResponseDtoV2.builder()
                .characterClassName(character.getCharacterClassName())
                .characterName(characterName)
                .build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
