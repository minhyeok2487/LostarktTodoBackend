package lostark.todo.controller.api;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterTodoDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.todo.Todo;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character")
@Api(tags = {"캐릭터 API"})
public class CharacterApiController {

    private final CharacterService characterService;
    private final TodoService todoService;
    private final ContentService contentService;

    @ApiOperation(value = "캐릭터 체크 변경",
            response = CharacterDayTodoDto.class)
    @PatchMapping("/check")
    public ResponseEntity updateEponaCheck(@AuthenticationPrincipal String username,
                                               @RequestBody CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterDayTodoDto.getCharacterName(), username);

        DayTodo updated = characterService.updateCheck(character, characterDayTodoDto);

        CharacterDayTodoDto responseDto = CharacterDayTodoDto.builder()
                .characterName(character.getCharacterName())
                .eponaCheck(updated.isEponaCheck())
                .chaosCheck(updated.getChaosCheck())
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 휴식 게이지 수정",
            response = CharacterDayTodoDto.class)
    @PatchMapping("/gauge")
    public ResponseEntity updateCharacterGauge(@AuthenticationPrincipal String username,
                                          @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterDayTodoDto.getCharacterName(), username);

        Character updateCharacter = characterService.updateGauge(character, characterDayTodoDto);
        CharacterDayTodoDto responseDto = CharacterDayTodoDto.builder()
                .characterName(updateCharacter.getCharacterName())
                .chaosGauge(updateCharacter.getDayTodo().getChaosGauge())
                .guardianGauge(updateCharacter.getDayTodo().getChaosGauge())
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 추가",
            response = CharacterResponseDto.class)
    @PostMapping("/week")
    public ResponseEntity addTodo(@AuthenticationPrincipal String username,
                                    @RequestBody CharacterTodoDto characterTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterTodoDto.getCharacterName(), username);

        int gold = contentService.findWeekGold(characterTodoDto.getContentName());

        Todo todo = todoService.addWeek(characterTodoDto, character, gold);

        return new ResponseEntity(todo, HttpStatus.OK);
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
