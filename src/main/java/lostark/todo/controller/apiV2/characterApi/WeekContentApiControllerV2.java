package lostark.todo.controller.apiV2.characterApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v2/character/week")
@Api(tags = {"캐릭터 API V2 - 주간 컨텐츠"})
public class WeekContentApiControllerV2 {

    private final CharacterService characterService;
    private final ContentService contentService;

    @ApiOperation(value = "캐릭터 주간 숙제 추가폼")
    @GetMapping("/form/{characterId}/{characterName}")
    public ResponseEntity getTodoForm(@AuthenticationPrincipal String username,
                                     @PathVariable long characterId, @PathVariable String characterName) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(characterId, characterName, username);

        // 아이템 레벨보다 작은 컨텐츠 불러옴
        List<WeekContent> allByWeekContent = contentService.findAllWeekContent(character.getItemLevel());

        if(allByWeekContent.isEmpty()) {
            throw new IllegalStateException("컨텐츠 불러오기 오류");
        }

        // 이미 등록된 컨텐츠면 true
        List<WeekContentDto> result = new ArrayList<>();

        for (WeekContent weekContent : allByWeekContent) {
            WeekContentDto weekContentDto = new WeekContentDto().toDto(weekContent);
            if(!character.getTodoV2List().isEmpty()) {
                for (TodoV2 todo : character.getTodoV2List()) {
                    if (todo.getWeekContent().equals(weekContent)) {
                        weekContentDto.setChecked(true);
                        break;
                    }
                }
            }
            result.add(weekContentDto);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }



    @ApiOperation(value = "캐릭터 주간 에포나 체크",
            notes = "'all'이 붙으면 전체 체크/해제",
            response = CharacterDto.class)
    @PatchMapping({"/epona/{all}","/epona"})
    public ResponseEntity updateWeekTodoEponaCheck(@AuthenticationPrincipal String username,
                                                   @PathVariable(required = false) String all,
                                                   @RequestBody CharacterDto characterDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(
                characterDto.getId(), characterDto.getCharacterName(), username);

        // all?
        if(all != null) {
            if (character.getWeekTodo().getWeekEpona() <3) {
                character.getWeekTodo().setWeekEpona(2);
            }
        }

        // Check 업데이트
        characterService.updateWeekEpona(character);

        return new ResponseEntity(new CharacterDto().toDtoV2(character), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 실마엘 교환 체크", response = CharacterDto.class)
    @PatchMapping("/silmael")
    public ResponseEntity updateSilmael(@AuthenticationPrincipal String username,
                                                   @RequestBody CharacterDto characterDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacter(
                characterDto.getId(), characterDto.getCharacterName(), username);

        // Check 업데이트
        characterService.updateSilmael(character);

        return new ResponseEntity(new CharacterDto().toDtoV2(character), HttpStatus.OK);
    }
}
