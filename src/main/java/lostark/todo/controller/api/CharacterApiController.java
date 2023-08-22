package lostark.todo.controller.api;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterGaugeDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/character")
@Api(tags = {"캐릭터 API"})
public class CharacterApiController {

    private final CharacterService characterService;

    @PatchMapping
    public ResponseEntity updateCharacter(@AuthenticationPrincipal String username,
                                          @RequestBody CharacterGaugeDto characterGaugeDto) {
        log.info("characterGaugeDto = {}", characterGaugeDto.toString());
        Character character = characterService.updateGauge(characterGaugeDto);
        CharacterResponseDto characterResponseDto = CharacterResponseDto.builder()
                .characterName(character.getCharacterName())
                .chaosGauge(character.getCharacterDayContent().getChaosGauge())
                .guardianGauge(character.getCharacterDayContent().getChaosGauge())
                .build();
        return new ResponseEntity(characterResponseDto, HttpStatus.OK);
    }
}
