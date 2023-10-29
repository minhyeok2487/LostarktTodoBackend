package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lostark.todo.controller.dto.characterDto.CharacterDayTodoDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.content.WeekContent;
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
     * 캐릭터 조회(member에 포함된 캐릭터인지 검증, id 형식)
     */
    public Character findCharacter(long characterId, String characterName, String username) {
        return characterRepository.findCharacterWithMember(characterId, username).orElseThrow(
                () -> new IllegalArgumentException("characterName = "+characterName+" / username = " + username + " : 존재하지 않는 캐릭터"));
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
        // 골드 획득 지정 캐릭터 : 서버별 6캐릭 이상인지 확인
        int goldCharacter = characterRepository.countByMemberAndServerNameAndGoldCharacterIsTrue(
                character.getMember(), character.getServerName());

        //골드획득 지정 캐릭터가 아닌데 6개가 넘으면
        if (!character.isGoldCharacter() && goldCharacter >= 6) {
            throw new IllegalArgumentException("골드 획득 지정 캐릭터는 6캐릭까지 가능합니다.");
        }
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

    public void extracted(List<Character> characters) {
        for (Character character : characters) {
            character.getTodoV2List().forEach(todoV2 -> {
                WeekContent weekContent = todoV2.getWeekContent();
                if(weekContent.getCoolTime()==2){
                    if(todoV2.getCoolTime()==2) {
                        if(todoV2.isChecked()) {
                            todoV2.setCoolTime(0);
                        } else {
                            todoV2.setCoolTime(1);
                        }
                    }
                    else {
                        todoV2.setCoolTime(2);
                    }
                }
                todoV2.setChecked(false);
            });
        }
    }

    /**
     * 캐릭터 데이터 업데이트
     */
    public Character updateCharacter(Character character, Character newCharacter) {
        return character.updateCharacter(newCharacter);
    }

    /**
     * 캐릭터 삭제
     */
    public void deleteCharacter(List<Character> beforeCharacterList, Character character) {
        beforeCharacterList.remove(character);
    }

    /**
     * 캐릭터 리스트 추가
     */
    public void addCharacterList(List<Character> addList, List<Character> removeList, Member member) {
        for (Character addCharacter : addList) {
            if (addCharacter.getCharacterImage() != null) { //이미지가 null이 아니면 => 캐릭터 닉네임이 바뀐 경우가 존재
                String characterImageId = extracted(addCharacter.getCharacterImage()); //이미지 url속 캐릭터 id(addList)
                for (Character before : removeList) { //삭제된 리스트 중에서
                    if (before.getCharacterImage() != null) { //이미지가 null이 아닌
                        String beforeCharacterImageId = extracted(before.getCharacterImage()); //이미지 url속 캐릭터 id(removeList)
                        if(beforeCharacterImageId.equals(characterImageId)) { //만약 id가 같다면
                            log.info("change characterName {} to {}", before.getCharacterName(), addCharacter.getCharacterName());
                            addCharacter.changeCharacter(before);
                        }
                    }
                }
            }
            member.addCharacter(addCharacter);
        }
    }


    private static String extracted(String url) {
        // URL에서 원하는 부분을 추출
        int startIndex = url.lastIndexOf('/') + 1; // '/' 다음 인덱스부터 시작
        int endIndex = url.indexOf(".png"); // ".png" 이전까지
        return url.substring(startIndex, endIndex);
    }

    /**
     * 주간 에포나 체크 업데이트
     */
    public void updateWeekEpona(Character character) {
        character.getWeekTodo().updateWeekEpona();
    }

    /**
     * 실마엘 교환 업데이트
     */
    public void updateSilmael(Character character) {
        character.getWeekTodo().updateSilmael();
    }

    /**
     * 큐브 티켓 업데이트
     */
    public void updateCubeTicket(Character character, int num) {
        character.getWeekTodo().updateCubeTicket(num);
    }
}
