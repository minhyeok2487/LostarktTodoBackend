package lostark.todo.controller.api.characterApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character/day-content")
@Api(tags = {"캐릭터 API - 일일 컨텐츠"})
public class DayContentApiController {

    private final CharacterService characterService;
    private final MarketService marketService;

    @ApiOperation(value = "캐릭터 일일컨텐츠 체크 업데이트",
            response = CharacterResponseDto.class)
    @PatchMapping("/check")
    public ResponseEntity updateDayTodoCheck(@AuthenticationPrincipal String username,
                                      @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterDayTodoDto.getCharacterName(), username);

        // Check 업데이트
        Character updateCharacter = characterService.updateCheck(character, characterDayTodoDto);

        return new ResponseEntity(new CharacterResponseDto().toDto(updateCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 휴식게이지 업데이트",
            response = CharacterResponseDto.class)
    @PatchMapping("/gauge")
    public ResponseEntity updateDayTodoGauge(@AuthenticationPrincipal String username,
                                             @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterDayTodoDto.getCharacterName(), username);

        // 휴식게이지 업데이트
        Character updateCharacter = characterService.updateGauge(character, characterDayTodoDto);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 업데이트된 휴식게이지로 예상 수익 계산
        Character resultCharacter = characterService.calculateDayTodo(updateCharacter, contentResource);

        return new ResponseEntity(new CharacterResponseDto().toDtoV3(resultCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 통계보기")
    @GetMapping("{characterName}/{category}")
    public ResponseEntity getDayTodoCheck(@AuthenticationPrincipal String username
            , @PathVariable("characterName") String characterName, @PathVariable("category") String category) {
        Character character = characterService.findCharacterWithMember(characterName, username);
        DayContent content = new DayContent();
        if (category.equals("카오스던전")) {
            content = character.getDayTodo().getChaos();
        }
        if (category.equals("가디언토벌")) {
            content = character.getDayTodo().getGuardian();
        }
        return new ResponseEntity(content, HttpStatus.OK);
    }
}
