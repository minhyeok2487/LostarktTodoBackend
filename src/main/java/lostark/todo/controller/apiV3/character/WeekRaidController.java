package lostark.todo.controller.apiV3.character;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.todoDto.raid.RaidGoldCheckRequestDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v3/character/week/raid")
@Api(tags = {"캐릭터 API V3 - 주간 레이드"})
public class WeekRaidController {

    private final CharacterService characterService;

    @ApiOperation(value = "주간 레이드 컨텐츠 골드 획득/해제")
    @PatchMapping("/gold-check")
    public ResponseEntity<?> updateRaidGoldCheck(@AuthenticationPrincipal String username,
                                                 @RequestBody RaidGoldCheckRequestDto requestDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.get(requestDto.getCharacterId(),
                requestDto.getCharacterName(), username);

        // 골드 체크 업데이트
        characterService.updateRaidGoldCheck(character, requestDto.getWeekCategory(), requestDto.isUpdateValue());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
