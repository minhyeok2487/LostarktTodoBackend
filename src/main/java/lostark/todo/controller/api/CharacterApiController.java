package lostark.todo.controller.api;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterGaugeDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character")
@Api(tags = {"캐릭터 API"})
public class CharacterApiController {

    private final CharacterService characterService;

    @ApiOperation(value = "캐릭터 휴식 게이지 수정",
            response = CharacterResponseDto.class)
    @PatchMapping("/gauge")
    public ResponseEntity updateCharacterGauge(@AuthenticationPrincipal String username,
                                          @RequestBody CharacterGaugeDto characterGaugeDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        characterService.findCharacterWithMember(characterGaugeDto.getCharacterName(), username);

        Character updateCharacter = characterService.updateGauge(characterGaugeDto);
        CharacterResponseDto characterResponseDto = CharacterResponseDto.builder()
                .characterName(updateCharacter.getCharacterName())
                .chaosGauge(updateCharacter.getCharacterDayContent().getChaosGauge())
                .guardianGauge(updateCharacter.getCharacterDayContent().getChaosGauge())
                .eponaGauge(updateCharacter.getCharacterDayContent().getEponaGauge())
                .build();
        return new ResponseEntity(characterResponseDto, HttpStatus.OK);
    }
}
