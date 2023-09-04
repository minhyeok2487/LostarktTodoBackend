package lostark.todo.controller.api;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterTodoDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoEnumDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todo.TodoContentName;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character")
@Api(tags = {"캐릭터 API"})
public class CharacterApiController {

    private final CharacterService characterService;
    private final TodoService todoService;
    private final ContentService contentService;
    private final MarketService marketService;

    @ApiOperation(value = "캐릭터 일일컨텐츠 체크 업데이트",
            response = CharacterDayTodoDto.class)
    @PatchMapping("/check")
    public ResponseEntity updateDayTodoCheck(@AuthenticationPrincipal String username,
                                      @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterDayTodoDto.getCharacterName(), username);

        // Check 업데이트
        DayTodo updated = characterService.updateCheck(character, characterDayTodoDto);

        CharacterDayTodoDto responseDto = CharacterDayTodoDto.builder()
                .characterName(character.getCharacterName())
                .eponaCheck(updated.isEponaCheck())
                .chaosCheck(updated.getChaosCheck())
                .guardianCheck(updated.getGuardianCheck())
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
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
        Map<String, Market> contentResource = marketService.getContentResource();

        // 일일 숙제 통계 가져오기
        Map<String, DayContent> dayContent = contentService.findDayContent();

        // 업데이트된 휴식게이지로 예상 수익 계산
        Character resultCharacter = characterService.calculateDayTodo(updateCharacter, contentResource, dayContent);

        CharacterResponseDto responseDto = CharacterResponseDto.builder()
                .characterName(resultCharacter.getCharacterName())
                .chaosGauge(resultCharacter.getDayTodo().getChaosGauge())
                .chaosGold(resultCharacter.getDayTodo().getChaosGold())
                .guardianGauge(resultCharacter.getDayTodo().getChaosGauge())
                .guardianGold(resultCharacter.getDayTodo().getGuardianGold())
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 통계보기")
    @GetMapping("/day-todo/{characterName}/{category}")
    public ResponseEntity getDayTodoCheck(@AuthenticationPrincipal String username
            , @PathVariable("characterName") String characterName, @PathVariable("category") String category) {
        Character character = characterService.findCharacter(characterName);
        String name = "";
        if (category.equals("카오스던전")) {
            name = character.getDayTodo().getChaosName();
        }
        if (category.equals("가디언토벌")) {
            name = character.getDayTodo().getGuardianName();
        }
        DayContent content = contentService.findContentByName(name);
        log.info(content.getName());
        return new ResponseEntity(content, HttpStatus.OK);
    }


    @ApiOperation(value = "캐릭터 주간 숙제 추가폼",
            response = TodoContentName.class)
    @GetMapping("/week/{characterName}")
    public ResponseEntity todoForm(@AuthenticationPrincipal String username,
                                  @PathVariable("characterName") String characterName) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);

        // 주간숙제 Enum 리스트 -> Dto로 변환
        List<TodoEnumDto> todoEnumDtoList = Arrays.stream(TodoContentName.values())
                .map(todoContentName -> TodoEnumDto.builder()
                        .name(todoContentName.name())
                        .category(todoContentName.getCategory())
                        .displayName(todoContentName.getDisplayName())
                        .exist(false)
                        .build())
                .collect(Collectors.toList());


        List<Todo> todoList = character.getTodoList();
        todoEnumDtoList.forEach(todoEnumDto -> {
            boolean exists = todoList.stream()
                    .anyMatch(todo -> todoEnumDto.getName().equals(todo.getContentName().name()));
            todoEnumDto.setExist(exists);
        });

        return ResponseEntity.ok(todoEnumDtoList);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 추가/제거")
    @PostMapping("/week")
    public ResponseEntity updateTodo(@AuthenticationPrincipal String username,
                                    @RequestBody CharacterTodoDto characterTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterTodoDto.getCharacterName(), username);

        String displayName = characterTodoDto.getContentName().getDisplayName();
        if (characterTodoDto.getContentName().getCategory().equals("아브렐슈드")) {
            String[] parts = displayName.split("\\s+", 2);
            displayName = parts[0];
        }

        int gold = contentService.findWeekGold(displayName, characterTodoDto.getContentName().getGate());
        todoService.updateWeek(characterTodoDto, character, gold);

        return new ResponseEntity(character, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 check 수정",
            response = TodoResponseDto.class)
    @PatchMapping("/week/check")
    public ResponseEntity updateWeekCheck(@AuthenticationPrincipal String username,
                                  @RequestBody TodoDto todoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(todoDto.getCharacterName(), username);

        Todo todo = todoService.updateWeekCheck(todoDto);
        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .id(todo.getId())
                .check(todo.isChecked())
                .build();
        return new ResponseEntity(todoResponseDto, HttpStatus.OK);
    }
}
