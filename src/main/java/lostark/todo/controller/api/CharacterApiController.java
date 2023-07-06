package lostark.todo.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.characterDto.CharacterSaveDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CharacterApiController {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;

    @GetMapping("/character/list/{username}")
    public ResponseEntity characterList(@PathVariable String username) {
        try {
            List<Character> characterList = memberService.findMemberSelected(username).getCharacters();
            List<CharacterReturnDto> characterReturnDtoList = new ArrayList<>(); //출력할 리스트
            for (Character character : characterList) {
                CharacterReturnDto characterReturnDto = new CharacterReturnDto(character);
                DayContent dayContent = contentService.getDayContentByLevel(characterReturnDto.getItemLevel());

                Market destruction = getMarketData(dayContent.getLevel(), "파괴석");
                Market guardian = getMarketData(dayContent.getLevel(), "수호석");
                Market leapStone = getMarketData(dayContent.getLevel(), "돌파석");

                CharacterReturnDto resultDto = characterService.calculateDayContent(characterReturnDto, destruction, guardian, leapStone, dayContent);
                characterReturnDtoList.add(resultDto);
            }
            return new ResponseEntity<>(characterReturnDtoList, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/character/save")
    public ResponseEntity characterSave(CharacterSaveDto characterSaveDto) {
        Character character = characterService.saveCharacter(characterSaveDto);
        return new ResponseEntity<>(character, HttpStatus.OK);
    }

    private Market getMarketData(double level, String categoryName) {
        if (level > 1580) {
            if (categoryName.equals("파괴석")) {
                return marketService.getMarketByName("정제된 파괴강석");
            }
            if (categoryName.equals("수호석")) {
                return marketService.getMarketByName("정제된 수호강석");
            }
            if (categoryName.equals("돌파석")) {
                return marketService.getMarketByName("찬란한 명예의 돌파석");
            }
        } else if (level >= 1490 && level < 1580) {
            if (categoryName.equals("파괴석")) {
                return marketService.getMarketByName("파괴강석");
            }
            if (categoryName.equals("수호석")) {
                return marketService.getMarketByName("수호강석");
            }
            if (categoryName.equals("돌파석")) {
                return marketService.getMarketByName("경이로운 명예의 돌파석");
            }
        } else {
            if (categoryName.equals("파괴석")) {
                return marketService.getMarketByName("파괴석 결정");
            }
            if (categoryName.equals("수호석")) {
                return marketService.getMarketByName("수호석 결정");
            }
            if (categoryName.equals("돌파석")) {
                return marketService.getMarketByName("위대한 명예의 돌파석");
            }
        }
        return null;
    }
}
