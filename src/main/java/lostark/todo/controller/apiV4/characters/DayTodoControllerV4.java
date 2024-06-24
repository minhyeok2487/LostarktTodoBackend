package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodoCategoryEnum;
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
@RequestMapping("/v4/character/day-todo")
@Api(tags = {"캐릭터 일일 숙제 Api"})
public class DayTodoControllerV4 {

    private final CharacterService characterService;
    private final MarketService marketService;

    @ApiOperation(value = "캐릭터 일일컨텐츠 체크 업데이트", response = CharacterResponse.class)
    @PatchMapping({"/check/{category}", "/check/{category}/{all}"})
    public ResponseEntity<?> updateDayTodoCheck(@AuthenticationPrincipal String username,
                                             @PathVariable("category") DayTodoCategoryEnum category,
                                             @PathVariable(value = "all", required = false) String all,
                                             @RequestBody @Valid CharacterDefaultDto characterDefaultDto) {

        Character updateCharacter = characterService.updateDayTodoCheck(
                username, characterDefaultDto, category, all != null);

        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 휴식게이지 업데이트",
            response = CharacterResponse.class)
    @PatchMapping("/gauge")
    public ResponseEntity<?> updateDayTodoGauge(@AuthenticationPrincipal String username,
                                             @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 휴식게이지 업데이트 후 예상 수익 계산
        Character updateCharacter = characterService.updateGauge(username, characterDayTodoDto, contentResource);

        return new ResponseEntity<>(CharacterResponse.toDto(updateCharacter), HttpStatus.OK);
    }
}
