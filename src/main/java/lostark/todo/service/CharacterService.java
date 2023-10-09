package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;

    public List<Character> findAll() {
        return characterRepository.findAll();
    }

    /**
     * 캐릭터 조회(member에 포함된 캐릭터인지 검증)
     */
    public Character findCharacterWithMember(String characterName, String username) {
        return characterRepository.findCharacterWithMember(characterName, username)
                .orElseThrow(() -> new IllegalArgumentException("characterName = "+characterName+" / username = " + username + " : 존재하지 않는 캐릭터"));
    }

    /**
     * 캐릭터 일일 컨텐츠 수익 계산(휴식게이지 포함)
     */
    public Character calculateDayTodo(Character character, Map<String, Market> contentResource) {
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
        // 카오스 던전 계산
        calculateChaos(character.getDayTodo().getChaos(), destruction, guardian, jewelry, character);

        // 가디언 토벌 계산
        calculateGuardian(character.getDayTodo().getGuardian(), destruction, guardian, leapStone, character);

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
     * 일일컨텐츠 휴식게이지 업데이트
     */
    public Character updateGauge(Character character, CharacterDayTodoDto characterDayTodoDto) {
        character.getDayTodo().updateGauge(characterDayTodoDto);
        return character;
    }


    /**
     * 일일컨텐츠 체크 업데이트
     */
    public Character updateCheck(Character character, CharacterDayTodoDto characterDayTodoDto) {
        character.getDayTodo().updateCheck(characterDayTodoDto);
        return character;
    }

    /**
     * 골드 획득 지정캐릭터확인
     */
    public int checkGoldCharacter(Member member, String servername) {
        return characterRepository.countByMemberAndServerNameAndGoldCharacterIsTrue(member, servername);
    }

    public Character updateGoldCharacter(Character character) {
        return character.updateGoldCharacter();
    }

    public Map<String, Long> findGroupServerNameCount(Member member) {
        List<Object[]> group = characterRepository.findCountGroupByServerName(member);
        Map<String, Long> resultMap = new HashMap<>();
        long count = 0L;
        for (Object[] result : group) {
            String serverName = (String) result[0];
            long characterCount = (long) result[1];
            count += characterCount;
            resultMap.put(serverName, characterCount);
        }

        // resultMap 내림차순 정렬
        Map<String, Long> sortedMap = new LinkedHashMap<>();
        resultMap.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEachOrdered(entry -> sortedMap.put(entry.getKey(), entry.getValue()));
        return sortedMap;
    }

    public List<Character> findCharacterListServerName(Member member, String serverName) {
        return characterRepository.findCharacterListServerName(member, serverName);
    }

    public int checkGoldCharacter(Character checkedCharacter) {
        return characterRepository.countByMemberAndServerNameAndGoldCharacterIsTrue(checkedCharacter.getMember(), checkedCharacter.getServerName());
    }

    public List<Character> updateChallenge(Member member, String serverName, String content) {
        List<Character> characterList = findCharacterListServerName(member, serverName);
        for (Character character : characterList) {
            character.updateChallenge(content);
        }
        return characterList;
    }

    public void updateSetting(Character character, String name, boolean value) {
        character.getSettings().update(name, value);
    }
}
