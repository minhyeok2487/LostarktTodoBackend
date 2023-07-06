package lostark.todo.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.CharacterReturnDto;
import lostark.todo.controller.dto.CharacterSaveDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
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

    @GetMapping("/character/list/{username}")
    public ResponseEntity characterList(@PathVariable String username) {
        try {
            List<Character> characterList = characterService.characterListByUsernameAndSelect(username);
            List<CharacterReturnDto> characterReturnDtoList = new ArrayList<>();
            for (Character character : characterList) {
                CharacterReturnDto characterReturnDto = new CharacterReturnDto(character);
                DayContent dayContent = contentService.getDayContentByLevel(characterReturnDto.getItemLevel());

                Market destruction = new Market();
                Market guardian = new Market();
                Market leapStone = new Market();
                if (dayContent.getLevel() > 1580) {
                    destruction = marketService.getMarketByName("정제된 파괴강석");
                    guardian = marketService.getMarketByName("정제된 수호강석");
                    leapStone = marketService.getMarketByName("찬란한 명예의 돌파석");
                } else if (dayContent.getLevel() >= 1490 && dayContent.getLevel() < 1580) {
                    destruction = marketService.getMarketByName("파괴강석");
                    guardian = marketService.getMarketByName("수호강석");
                    leapStone = marketService.getMarketByName("경이로운 명예의 돌파석");
                } else {
                    destruction = marketService.getMarketByName("파괴석 결정");
                    guardian = marketService.getMarketByName("수호석 결정");
                    leapStone = marketService.getMarketByName("위대한 명예의 돌파석");
                }
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
}
