package lostark.todo.controller.apiController;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.controller.dto.contentDto.DayContentCountDto;
import lostark.todo.controller.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private final MemberService memberService;

    @ApiOperation(value = "회원에 등록된 캐릭터리스트 가져옴",
            notes="휴식게이지를 참고하여 일일컨텐츠 수익 계산하여 함께 리턴")
    @GetMapping("/{username}")
    public ResponseEntity characterList(@ApiParam(value = "유저 네임", required = true) @PathVariable String username) {
        try {
            // header : username으로 연결된 캐릭터리스트 중 선택할 리스트 가져옴
            List<Character> characterList = memberService.findMemberAndCharacter(username);

            // 거래소 데이터 가져옴(Map)
            Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(marketService.dayContentResource());

            // ItemLevel이 1415이상인 캐릭터는 레벨에 맞는 일일 컨텐츠 가져온후 계산
            List<CharacterReturnDto> characterReturnDtoList = contentService.calculateDayContent(characterList, contentResource);

            // 일일숙제 선택된 캐릭터들
            // Profit 순서대로 정렬하기
            List<SortedDayContentProfitDto> sortedDayContentProfit = contentService.sortDayContentProfit(characterReturnDtoList);

            // Profit 합 구하기
            double sum = 0;
            for (SortedDayContentProfitDto dto : sortedDayContentProfit) {
                sum += dto.getProfit();
            }
            sum = Math.round(sum * 100.0) / 100.0;

            // 결과 출력
            CharactersReturnDto charactersReturnDto = new CharactersReturnDto();
            charactersReturnDto.setCharacters(characterReturnDtoList);
            charactersReturnDto.setSumDayContentProfit(sum);
            charactersReturnDto.setSortedDayContentProfitDtoList(sortedDayContentProfit);

            return new ResponseEntity<>(charactersReturnDto, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 캐릭터 데이터 갱신
     */
    @PatchMapping()
    public ResponseEntity characterSave(@RequestBody CharacterRequestDto characterRequestDto) {
        System.out.println("characterSaveDto.getCharacterName() = " + characterRequestDto.getCharacterName());
        Character character = characterService.updateCharacter(characterRequestDto);
        Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(marketService.dayContentResource());
        CharacterReturnDto result = contentService.calculateDayContentOne(character, contentResource);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 일일컨텐츠 체크 변경
     * 1수보다 작으면 -> 2수
     * 2수 -> 0수
     */
    @PatchMapping("/dayContent")
    public ResponseEntity characterCheck(@RequestBody DayContentCountDto dto) {
        CharacterReturnDto result = characterService.updateDayContentCheck(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 일일컨텐츠 셀렉트 출력과 변경
     */
    @GetMapping("/dayContent/selected/{characterName}")
    public ResponseEntity dayContentSelected(@PathVariable String characterName) {
        return new ResponseEntity<>(characterService.readSelected(characterName), HttpStatus.OK);
    }

    @PatchMapping("/dayContent/selected/{characterName}")
    public ResponseEntity changeDayContentSelected(@RequestBody DayContentSelectedDto dto, @PathVariable String characterName) {
        return new ResponseEntity<>(characterService.updateSelected(dto, characterName), HttpStatus.OK);
    }


}
