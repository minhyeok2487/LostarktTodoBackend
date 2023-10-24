package lostark.todo.controller.api.characterApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.todoV2.TodoV2;
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
@RequestMapping("/character/week/v3")
@Api(tags = {"캐릭터 API - 주간 컨텐츠 V3"})
public class WeekContentV3ApiController {

    private final CharacterService characterService;
    private final TodoService todoService;
    private final ContentService contentService;
    private final MemberService memberService;

    @ApiOperation(value = "캐릭터 주간 숙제 추가폼 V3")
    @GetMapping("{characterName}")
    public ResponseEntity todoFormV3(@AuthenticationPrincipal String username,
                                     @PathVariable("characterName") String characterName) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);

        // 아이템 레벨보다 작은 컨텐츠 불러옴
        List<WeekContent> allByWeekContent = contentService.findAllByWeekContentWithItemLevelV2(character.getItemLevel());

        // 이미 등록된 컨텐츠면 true
        List<WeekContentDto> result = new ArrayList<>();
        for (WeekContent weekContent : allByWeekContent) {
            WeekContentDto weekContentDto = new WeekContentDto().toDto(weekContent);
            for (TodoV2 todo : character.getTodoV2List()) {
                if (todo.getWeekContent().equals(weekContent)) {
                    weekContentDto.setChecked(true);
                }
            }
            result.add(weekContentDto);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 추가/제거 V3")
    @PostMapping("{characterName}")
    public ResponseEntity updateTodo_V3(@AuthenticationPrincipal String username,
                                        @PathVariable("characterName") String characterName,
                                        @RequestBody WeekContentDto weekContentDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);
        WeekContent weekContent = (WeekContent) contentService.findById(weekContentDto.getId());

        todoService.updateWeek_V3(character, weekContent);

        return new ResponseEntity(new CharacterDto().toDtoV2(character), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 전체 추가/제거 V3")
    @PostMapping("/all/{characterName}")
    public ResponseEntity updateTodoV3All(@AuthenticationPrincipal String username,
                                          @PathVariable("characterName") String characterName,
                                          @RequestBody List<WeekContentDto> weekContentDtoList) {
        Character character = characterService.findCharacterWithMember(characterName, username);
        List<WeekContent> weekContentList = contentService.findAllByCategoryAndWeekCategory(character.getItemLevel(),
                weekContentDtoList.get(0).getWeekCategory(), weekContentDtoList.get(0).getWeekContentCategory());
        todoService.updateWeekAllV3(character, weekContentList);

        return new ResponseEntity(new CharacterDto().toDtoV2(character), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 check 수정")
    @PatchMapping("/check")
    public ResponseEntity updateWeekCheckV3(@AuthenticationPrincipal String username,
                                            @RequestBody TodoDto todoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(todoDto.getCharacterName(), username);
        todoService.updateWeekCheckV3(character, todoDto.getWeekCategory(), todoDto.getCurrentGate(), todoDto.getTotalGate());
        return new ResponseEntity(new CharacterDto().toDtoV2(character), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 check 수정 All")
    @PatchMapping("/check/all")
    public ResponseEntity updateWeekCheckAllV3(@AuthenticationPrincipal String username,
                                            @RequestBody TodoDto todoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(todoDto.getCharacterName(), username);
        todoService.updateWeekCheckAllV3(character, todoDto.getWeekCategory());
        return new ResponseEntity(new CharacterDto().toDtoV2(character), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 레이드 message 수정",
            response = TodoResponseDto.class)
    @PatchMapping("/message")
    public ResponseEntity updateWeekMessageV3(@AuthenticationPrincipal String username,
                                            @RequestBody TodoDto todoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(todoDto.getCharacterName(), username);
        TodoV2 todo = todoService.updateWeekMessageV3(todoDto);
        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .id(todo.getId())
                .message(todo.getMessage())
                .build();
        return new ResponseEntity(todoResponseDto, HttpStatus.OK);
    }

}
