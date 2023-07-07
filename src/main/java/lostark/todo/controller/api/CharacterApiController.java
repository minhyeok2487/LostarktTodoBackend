package lostark.todo.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.characterDto.CharacterSaveDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CharacterApiController {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;

    private static List<String> makeDayContentResourceNames() {
        List<String> dayContentResource = new ArrayList<>();
        dayContentResource.add("정제된 파괴강석");
        dayContentResource.add("정제된 수호강석");
        dayContentResource.add("찬란한 명예의 돌파석");

        dayContentResource.add("파괴강석");
        dayContentResource.add("수호강석");
        dayContentResource.add("경이로운 명예의 돌파석");

        dayContentResource.add("파괴석 결정");
        dayContentResource.add("수호석 결정");
        dayContentResource.add("위대한 명예의 돌파석");
        return dayContentResource;
    }

    @GetMapping("/character/selectedList")
    public ResponseEntity characterList(HttpServletRequest request) {
        try {
            // header : username으로 연결된 캐릭터리스트 중 선택할 리스트 가져옴
            List<Character> characterList = memberService.findMemberSelected(request.getHeader("username")).getCharacters();

            // 거래소 데이터 가져옴(Map)
            Map<String , MarketContentResourceDto> contentResource = marketService.getContentResource(makeDayContentResourceNames());

            List<CharacterReturnDto> characterReturnDtoList = new ArrayList<>(); //출력할 리스트

            for (Character character : characterList) {
                // character 엔티티로 dto 객체 생성
                CharacterReturnDto characterReturnDto = new CharacterReturnDto(character);

                // 객체 레벨에 맞는 일일 컨텐츠 가져옴
                Map<Category, DayContent> contentMap = contentService.getDayContentByLevel(characterReturnDto.getItemLevel());

                // 계산
                calculateDayContent(characterReturnDto, contentMap, contentResource);

                characterReturnDtoList.add(characterReturnDto);
                System.out.println("characterReturnDto = " + characterReturnDto.toString());
            }
            return new ResponseEntity<>(characterReturnDtoList, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/character")
    public ResponseEntity characterSave(CharacterSaveDto characterSaveDto) {
        Character character = characterService.saveCharacter(characterSaveDto);
        return new ResponseEntity<>(character, HttpStatus.OK);
    }

    private void calculateDayContent(CharacterReturnDto characterReturnDto,
                                     Map<Category, DayContent> contentMap,
                                     Map<String , MarketContentResourceDto> contentResource) {
        MarketContentResourceDto destruction = null;
        MarketContentResourceDto guardian = null;
        MarketContentResourceDto leapStone = null;
        if (characterReturnDto.getItemLevel() >= 1415) {
            destruction = contentResource.get("파괴석 결정");
            guardian = contentResource.get("수호석 결정");
            leapStone = contentResource.get("위대한 명예의 돌파석");
        }
        if (characterReturnDto.getItemLevel() >= 1540) {
            destruction = contentResource.get("파괴강석");
            guardian = contentResource.get("수호강석");
            leapStone = contentResource.get("경이로운 명예의 돌파석");
        }
        if (characterReturnDto.getItemLevel() >= 1580) {
            destruction = contentResource.get("정제된 파괴강석");
            guardian = contentResource.get("정제된 수호강석");
            leapStone = contentResource.get("찬란한 명예의 돌파석");
        }

        calculateChaos(characterReturnDto, destruction, guardian, contentMap.get(Category.카오스던전));
        calculateGuardian(characterReturnDto, destruction, guardian, leapStone, contentMap.get(Category.가디언토벌));
    }


    public void calculateChaos(CharacterReturnDto characterReturnDto, MarketContentResourceDto destruction, MarketContentResourceDto guardian, DayContent dayContent) {
        double price = 0;
        if (characterReturnDto.getChaosGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
            }
        } else if (characterReturnDto.getChaosGauge() < 40 && characterReturnDto.getChaosGauge() >= 20) {
            for (int i = 0; i < 3; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
            }
        } else {
            for (int i = 0; i < 2; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
            }
        }
        price += dayContent.getGold();
        characterReturnDto.setChaosName(dayContent.getName());
        characterReturnDto.setChaosProfit(price);
    }

    private void calculateGuardian(CharacterReturnDto characterReturnDto,
                                   MarketContentResourceDto destruction,
                                   MarketContentResourceDto guardian,
                                   MarketContentResourceDto leapStone,
                                   DayContent dayContent) {
        double price = 0;
        if (characterReturnDto.getGuardianGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(leapStone, dayContent.getLeapStone(), price);
            }
        } else if (characterReturnDto.getGuardianGauge() < 40 && characterReturnDto.getGuardianGauge() >= 20) {
            for (int i = 0; i < 3; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(leapStone, dayContent.getLeapStone(), price);
            }
        } else {
            for (int i = 0; i < 2; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(leapStone, dayContent.getLeapStone(), price);
            }
        }
        characterReturnDto.setGuardianName(dayContent.getName());
        characterReturnDto.setGuardianProfit(price);
    }

    private double calculateBundle(MarketContentResourceDto dto, double count, double price) {
        price += (dto.getRecentPrice() * count) / dto.getBundleCount();
        return Math.round(price * 100.0) / 100.0;
    }
}
