package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;

    public List<Character> findAll() {
        return characterRepository.findAll();
    }

    public List<Character> findByMember(Member member) {
        return characterRepository.findByMember(member);
    }

    /**
     * 캐릭터 조회
     */
    public Character findCharacter(String characterName) {
        return characterRepository.findByCharacterName(characterName)
                .orElseThrow(() -> new IllegalArgumentException("characterName = "+characterName+" : 존재하지 않는 캐릭터"));
    }

    /**
     * 캐릭터 조회(member에 포함된 캐릭터인지 검증)
     */
    public Character findCharacterWithMember(String characterName, String username) {
        return characterRepository.findCharacterWithMember(characterName, username)
                .orElseThrow(() -> new IllegalArgumentException("characterName = "+characterName+" / username = " + username + " : 존재하지 않는 캐릭터"));
    }

    /**
     * 일일 숙제 예상 골드 계산
     */
    public Character calculateDayTodo(Character character,
                                 Map<String, Market> contentResource,
                                 Map<String, DayContent> dayContent) {
        Market jewelry = contentResource.get("1레벨");
        Market destruction;
        Market guardian;
        Market leapStone;
        if (character.getItemLevel() >= 1415 && character.getItemLevel() < 1540) {
            destruction = contentResource.get("파괴석 결정");
            guardian = contentResource.get("수호석 결정");
            leapStone = contentResource.get("위대한 명예의 돌파석");
        } else if (character.getItemLevel() >= 1540 && character.getItemLevel() < 1580) {
            destruction = contentResource.get("파괴강석");
            guardian = contentResource.get("수호강석");
            leapStone = contentResource.get("경이로운 명예의 돌파석");
        } else {
            destruction = contentResource.get("정제된 파괴강석");
            guardian = contentResource.get("정제된 수호강석");
            leapStone = contentResource.get("찬란한 명예의 돌파석");
        }

        DayContent chaosName = dayContent.get(character.getDayTodo().getChaosName());
        calculateChaos(chaosName, destruction, guardian, jewelry, character);

        DayContent guardianName = dayContent.get(character.getDayTodo().getGuardianName());
        calculateGuardian(guardianName, destruction, guardian, leapStone, character);
        return character;
    }

    private void calculateChaos(DayContent dayContent, Market destruction, Market guardian, Market jewelry, Character character) {
        double price = 0;
        price += destruction.getRecentPrice() * dayContent.getDestructionStone() / destruction.getBundleCount();
        price += guardian.getRecentPrice() * dayContent.getGuardianStone() / guardian.getBundleCount();
        price += jewelry.getRecentPrice() * dayContent.getJewelry();

        int chaosGauge = character.getDayTodo().getChaosGauge();
        if (chaosGauge >= 40) {
            price = price*4;
        } else if (chaosGauge < 40 && chaosGauge >= 20) {
            price = price*3;
        } else {
            price = price*2;
        }

        price = Math.round(price * 100.0) / 100.0;
        character.getDayTodo().setChaosGold(price);
    }

    private void calculateGuardian(DayContent dayContent, Market destruction, Market guardian, Market leapStone, Character character) {
        double price = 0;
        price += destruction.getRecentPrice() * dayContent.getDestructionStone() / destruction.getBundleCount();
        price += guardian.getRecentPrice() * dayContent.getGuardianStone() / guardian.getBundleCount();
        price += leapStone.getRecentPrice() * dayContent.getLeapStone() / leapStone.getBundleCount();

        int guardianGauge = character.getDayTodo().getGuardianGauge();
        if (guardianGauge >= 20) {
            price = price*2;
        }

        price = Math.round(price * 100.0) / 100.0;
        character.getDayTodo().setGuardianGold(price);
    }

    /**
     * 휴식게이지 업데이트
     */
    public Character updateGauge(Character character, CharacterDayTodoDto characterDayTodoDto) {
        character.getDayTodo().updateGauge(characterDayTodoDto);
        return character;
    }

    /**
     * 수익 계산
     */
    public void calculateProfit(
            List<CharacterResponseDto> characterResponseDtoList,
            Map<String, Market> contentResource) {
        Market destruction = null;
        Market guardian = null;
        Market leapStone = null;
        for (CharacterResponseDto characterResponseDto : characterResponseDtoList) {
            if (characterResponseDto.getItemLevel() >= 1415) {
                destruction = contentResource.get("파괴석 결정");
                guardian = contentResource.get("수호석 결정");
                leapStone = contentResource.get("위대한 명예의 돌파석");
            }
            if (characterResponseDto.getItemLevel() >= 1540) {
                destruction = contentResource.get("파괴강석");
                guardian = contentResource.get("수호강석");
                leapStone = contentResource.get("경이로운 명예의 돌파석");
            }
            if (characterResponseDto.getItemLevel() >= 1580) {
                destruction = contentResource.get("정제된 파괴강석");
                guardian = contentResource.get("정제된 수호강석");
                leapStone = contentResource.get("찬란한 명예의 돌파석");
            }
            Market jewelry = contentResource.get("1레벨");
//            calculateChaos(characterResponseDto, destruction, guardian, jewelry);
//            calculateGuardian(characterResponseDto, destruction, guardian, leapStone);
        }

    }

    public DayTodo updateCheck(Character character, CharacterDayTodoDto characterDayTodoDto) {
        return character.getDayTodo().updateCheck(characterDayTodoDto); // 변경
    }

    public List<Character> calculateDayTodoV2(List<Character> characterList, Map<String, Market> contentResource) {
        Market jewelry = contentResource.get("1레벨");
        Market destruction;
        Market guardian;
        Market leapStone;
        for (Character character : characterList) {
            if (character.getItemLevel() >= 1415 && character.getItemLevel() < 1540) {
                destruction = contentResource.get("파괴석 결정");
                guardian = contentResource.get("수호석 결정");
                leapStone = contentResource.get("위대한 명예의 돌파석");
            } else if (character.getItemLevel() >= 1540 && character.getItemLevel() < 1580) {
                destruction = contentResource.get("파괴강석");
                guardian = contentResource.get("수호강석");
                leapStone = contentResource.get("경이로운 명예의 돌파석");
            } else {
                destruction = contentResource.get("정제된 파괴강석");
                guardian = contentResource.get("정제된 수호강석");
                leapStone = contentResource.get("찬란한 명예의 돌파석");
            }
            calculateChaos(character.getDayTodo().getChaos(), destruction, guardian, jewelry, character);
            calculateGuardian(character.getDayTodo().getGuardian(), destruction, guardian, leapStone, character);
        }
        return characterList;
    }

    public void deleteCharacter(Member member) {
        characterRepository.deleteByMember(member);
    }
}
