package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodoCategoryEnum;
import lostark.todo.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/character/day-todo")
@Api(tags = {"캐릭터 일일 숙제 Api"})
public class DayTodoControllerV4 {

    private final CharacterService characterService;

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
}
