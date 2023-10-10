package lostark.todo.controller.api.characterApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.todo.Todo;
import lostark.todo.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character/week/v2")
@Api(tags = {"캐릭터 API - 주간 컨텐츠 V2"})
public class WeekContentV2ApiController {

    private final CharacterService characterService;
    private final TodoService todoService;
    private final ContentService contentService;

    @ApiOperation(value = "캐릭터 주간 숙제 추가폼")
    @GetMapping("{characterName}")
    public ResponseEntity todoForm(@AuthenticationPrincipal String username,
                                   @PathVariable("characterName") String characterName) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);

        // 아이템 레벨보다 작은 컨텐츠 불러옴
        List<WeekContent> allByWeekContent = contentService.findAllByWeekContentWithItemLevel(character.getItemLevel());

        List<WeekContentDto> result = new ArrayList<>();
        for (WeekContent weekContent : allByWeekContent) {
            WeekContentDto weekContentDto = new WeekContentDto().toDto(weekContent);
            for (Todo todo : character.getTodoList()) {
                if (todo.getName().equals(weekContentDto.getName())) {
                    weekContentDto.setChecked(true);
                }
            }
            result.add(weekContentDto);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 추가/제거")
    @PostMapping("{characterName}")
    public ResponseEntity updateTodo_V2(@AuthenticationPrincipal String username,
                                        @PathVariable("characterName") String characterName,
                                        @RequestBody WeekContentDto weekContentDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);

        todoService.updateWeek_V2(character, weekContentDto);

        return new ResponseEntity(new CharacterResponseDto().toDto(character), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 check",
            response = TodoResponseDto.class)
    @PatchMapping("/check")
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

    @ApiOperation(value = "캐릭터 주간 숙제 message 수정",
            response = TodoResponseDto.class)
    @PatchMapping("/message")
    public ResponseEntity updateWeekMessage(@AuthenticationPrincipal String username,
                                            @RequestBody TodoDto todoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(todoDto.getCharacterName(), username);
        Todo todo = todoService.updateWeekMessage(todoDto);
        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .id(todo.getId())
                .message(todo.getMessage())
                .build();
        return new ResponseEntity(todoResponseDto, HttpStatus.OK);
    }
}
