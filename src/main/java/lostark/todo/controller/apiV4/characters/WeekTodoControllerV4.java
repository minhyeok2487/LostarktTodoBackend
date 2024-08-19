package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/character/week-todo")
@Api(tags = {"캐릭터 주간 숙제 Api"})
public class WeekTodoControllerV4 {

    private final CharacterService characterService;
    private final ContentService contentService;

    @ApiOperation(value = "캐릭터 주간 숙제 추가폼", response = WeekContentDto.class)
    @GetMapping("/form/{characterId}/{characterName}")
    public ResponseEntity<?> getTodoForm(@AuthenticationPrincipal String username,
                                         @PathVariable long characterId, @PathVariable String characterName) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.get(characterId, characterName, username);

        // 아이템 레벨보다 작은 컨텐츠 불러옴
        List<WeekContent> weekContents = contentService.findAllWeekContent(character.getItemLevel());

        List<WeekContentDto> result = weekContents.stream()
                .map(weekContent -> {
                    WeekContentDto weekContentDto = new WeekContentDto().toDto(weekContent);
                    character.getTodoV2List().stream()
                            .filter(todo -> todo.getWeekContent().equals(weekContent))
                            .findFirst()
                            .ifPresent(todo -> {
                                weekContentDto.setChecked(true);
                                weekContentDto.setGoldCheck(todo.isGoldCheck());
                            });
                    return weekContentDto;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
