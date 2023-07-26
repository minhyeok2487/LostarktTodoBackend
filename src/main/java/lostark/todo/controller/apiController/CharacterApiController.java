package lostark.todo.controller.apiController;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/character")
@Api(tags = {"캐릭터 관련 REST API"})
public class CharacterApiController {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;

    @ApiOperation(value = "캐릭터 데이터 조회", response = CharacterResponseDto.class)
    @GetMapping("/{characterName}")
    public ResponseEntity findCharacter(@PathVariable String characterName) {
        // 캐릭터 정보 가져옴
        // 일일컨텐츠 이름, 예상 수익 없음
        Character character = characterService.findCharacter(characterName);

        // 계산 후 리턴
        return new ResponseEntity<>(calculateCharacter(character), HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일 컨텐츠 수정",
            notes="일일 컨텐츠 체크, 휴식게이지 수정", response = CharacterResponseDto.class)
    @PatchMapping("/{characterName}/dayContent")
    public ResponseEntity updateCharacter(@PathVariable String characterName, @RequestBody CharacterDayContentResponseDto characterDayContentResponseDto) {
        // 일일 컨텐츠 체크, 휴식게이지 갱신
        Character character = characterService.updateCharacter(characterName, characterDayContentResponseDto);
        return new ResponseEntity<>(calculateCharacter(character), HttpStatus.OK);
    }

    @ApiOperation(value = "일일컨텐츠 체크 일괄 변경",
            notes="1수보다 작으면 -> 2수 \n 2수 -> 0수", response = CharacterResponseDto.class)
    @PatchMapping("/{characterName}/dayContent/{category}/check")
    public ResponseEntity updateCharacterCheck(@PathVariable String characterName,
                                         @ApiParam(allowableValues = "카오스던전, 가디언토벌") @PathVariable Category category) {
        Character character = characterService.updateDayContentCheck(characterName, category);
        return new ResponseEntity<>(calculateCharacter(character), HttpStatus.OK);
    }


    @ApiOperation(value = "일일컨텐츠 셀렉트 변경",
            notes = "true -> 할 컨텐츠\n false -> 안할 컨텐츠", response = CharacterResponseDto.class)
    @PatchMapping("/{characterName}/dayContent/{category}/selected")
    public ResponseEntity updateCharacterSelected(@PathVariable String characterName,
                                                  @ApiParam(allowableValues = "카오스던전, 가디언토벌") @PathVariable Category category) {
        Character character = characterService.updateDayContentSelected(characterName, category);
        return new ResponseEntity<>(calculateCharacter(character), HttpStatus.OK);
    }


    /**
     * 캐릭터의 일일 컨텐츠 이름과 예상 수익을 계산하는 메소드
     */
    private CharacterResponseDto calculateCharacter(Character character) {
        // 거래소 데이터 가져옴(Map)
        Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(marketService.dayContentResource());

        // 일일컨텐츠 이름 저장하고 예상 수익 계산 후 리턴
        return contentService.calculateDayContentOne(character, contentResource);
    }
}
