package lostark.todo.controller.apiV3.character;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.market.Market;
import lostark.todo.event.entity.character.DayContentCheckEvent;
import lostark.todo.event.entity.EventType;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MarketService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v3/character/day-content")
@Api(tags = {"캐릭터 API - 일일 컨텐츠"})
public class DayContentApiController {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ApplicationEventPublisher eventPublisher;

    // TODO 추후 삭제
    @ApiOperation(value = "캐릭터 일일컨텐츠 체크 업데이트", response = CharacterDto.class)
    @PatchMapping({"/check/{category}", "/check/{category}/{all}"})
    public ResponseEntity<?> updateDayTodoCheck(@AuthenticationPrincipal String username,
                                                @PathVariable("category") String category,
                                                @PathVariable(value = "all", required = false) String all,
                                                @RequestBody @Valid CharacterDefaultDto characterDefaultDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(
                characterDefaultDto.getCharacterId(), characterDefaultDto.getCharacterName(), username);

        // 이전 값 저장
        int beforeGauge = getBeforeGauge(character.getDayTodo(), category);
        int beforeCheck = getBeforeCheck(character.getDayTodo(), category);

        // Check 업데이트
        Character updateCharacter;

        if (all == null) {
            updateCharacter = characterService.updateCheck(character, category);
        } else {
            updateCharacter = characterService.updateCheckAll(character, category);
        }

        // 이벤트 발생
        eventPublisher.publishEvent(DayContentCheckEvent.builder()
                .source(eventPublisher)
                .eventType(EventType.checkDayContent)
                .character(character)
                .category(category)
                .beforeGauge(beforeGauge)
                .beforeCheck(beforeCheck)
                .build());

        return new ResponseEntity<>(new CharacterDto().toDtoV2(updateCharacter), HttpStatus.OK);
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

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 휴식게이지 업데이트 후 예상 수익 계산
        Character updateCharacter = characterService.updateGauge(character, characterDayTodoDto, contentResource);

        return new ResponseEntity(new CharacterDto().toDtoV2(updateCharacter), HttpStatus.OK);
    }


    private int getBeforeGauge(DayTodo dayTodo, String category) {
        switch (category) {
            case "epona":
                return dayTodo.getEponaGauge();
            case "chaos":
                return dayTodo.getChaosGauge();
            case "guardian":
                return dayTodo.getGuardianGauge();
            default:
                return 0;
        }
    }

    private int getBeforeCheck(DayTodo dayTodo, String category) {
        switch (category) {
            case "epona":
                return dayTodo.getEponaCheck2();
            case "chaos":
                return dayTodo.getChaosCheck();
            case "guardian":
                return dayTodo.getGuardianCheck();
            default:
                return 0;
        }
    }

}
