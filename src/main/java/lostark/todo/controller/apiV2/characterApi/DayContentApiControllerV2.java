package lostark.todo.controller.apiV2.characterApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.market.Market;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v2/character/day-content")
@Api(tags = {"캐릭터 API V2 - 일일 컨텐츠"})
public class DayContentApiControllerV2 {

    private final CharacterService characterService;
    private final MarketService marketService;

    @ApiOperation(value = "캐릭터 일일컨텐츠 체크 업데이트", response = CharacterDto.class)
    @PatchMapping("/check")
    public ResponseEntity updateDayTodoCheck(@AuthenticationPrincipal String username,
                                      @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(
                characterDayTodoDto.getCharacterId(), characterDayTodoDto.getCharacterName(), username);

        // Check 업데이트
        Character updateCharacter = characterService.updateCheck(character, characterDayTodoDto);

        return new ResponseEntity(new CharacterDto().toDtoV2(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 휴식게이지 업데이트",
            response = CharacterDto.class)
    @PatchMapping("/gauge")
    public ResponseEntity updateDayTodoGauge(@AuthenticationPrincipal String username,
                                             @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(
                characterDayTodoDto.getCharacterId(), characterDayTodoDto.getCharacterName(), username);

        // 휴식게이지 업데이트
        Character updateCharacter = characterService.updateGauge(character, characterDayTodoDto);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 업데이트된 휴식게이지로 예상 수익 계산
        Character resultCharacter = characterService.calculateDayTodo(updateCharacter, contentResource);

        return new ResponseEntity(new CharacterDto().toDtoV2(resultCharacter), HttpStatus.OK);
    }

}
