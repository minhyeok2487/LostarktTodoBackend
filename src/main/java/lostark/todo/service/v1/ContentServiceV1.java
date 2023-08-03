package lostark.todo.service.v1;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDtoV1;
import lostark.todo.controller.v1.dto.contentDto.DayContentProfitDtoV1;
import lostark.todo.controller.v1.dto.contentDto.SortedDayContentProfitDtoV1;
import lostark.todo.controller.v1.dto.marketDto.MarketContentResourceDtoV1;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentServiceV1 {

    private final ContentRepository contentRepository;

    public List<CharacterResponseDtoV1> calculateDayContentV2(List<Character> characterList) {
        //출력할 리스트
        List<CharacterResponseDtoV1> characterResponseDtoV1List = new ArrayList<>();

        for (Character character : characterList) {
            // 캐릭터 레벨에 따른 일일컨텐츠
            Map<Category, DayContent> contentMap = getDayContentByLevel(character.getItemLevel());

            // character 엔티티로 dto 객체 생성
            CharacterResponseDtoV1 characterResponseDtoV1 = CharacterResponseDtoV1.builder()
                    .id(character.getId())
                    .characterName(character.getCharacterName())
                    .characterImage(character.getCharacterImage())
                    .characterClassName(character.getCharacterClassName())
                    .itemLevel(character.getItemLevel())
                    .chaosSelected(character.getCharacterDayContent().isChaosSelected())
                    .chaosCheck(character.getCharacterDayContent().getChaosCheck())
                    .chaosGauge(character.getCharacterDayContent().getChaosGauge())
                    .guardianSelected(character.getCharacterDayContent().isGuardianSelected())
                    .guardianCheck(character.getCharacterDayContent().getGuardianCheck())
                    .guardianGauge(character.getCharacterDayContent().getGuardianGauge())
                    .build();

            characterResponseDtoV1List.add(characterResponseDtoV1);
            }
        return characterResponseDtoV1List;
    }

    /**
     * 캐릭터 레벨에 따른 일일컨텐츠
     */
    public Map<Category, DayContent> getDayContentByLevel(double level) {
        DayContent chaosContent = contentRepository.findDayContentByLevel(level, Category.카오스던전).get(0);
        DayContent guardianContent = contentRepository.findDayContentByLevel(level, Category.가디언토벌).get(0);

        Map<Category, DayContent> dayContentMap = new HashMap<>();
        dayContentMap.put(Category.카오스던전, chaosContent);
        dayContentMap.put(Category.가디언토벌, guardianContent);
        return dayContentMap;
    }

    public List<CharacterResponseDtoV1> calculateDayContent(
            List<Character> characterList, Map<String, MarketContentResourceDtoV1> contentResource) {
        List<CharacterResponseDtoV1> characterResponseDtoV1List = new ArrayList<>(); //출력할 리스트

        for (Character character : characterList) {
            // character 엔티티로 dto 객체 생성
            CharacterResponseDtoV1 characterResponseDtoV1 = CharacterResponseDtoV1.builder()
                    .id(character.getId())
                    .characterName(character.getCharacterName())
                    .characterImage(character.getCharacterImage())
                    .characterClassName(character.getCharacterClassName())
                    .itemLevel(character.getItemLevel())
                    .chaosSelected(character.getCharacterDayContent().isChaosSelected())
                    .chaosCheck(character.getCharacterDayContent().getChaosCheck())
                    .chaosGauge(character.getCharacterDayContent().getChaosGauge())
                    .guardianSelected(character.getCharacterDayContent().isGuardianSelected())
                    .guardianCheck(character.getCharacterDayContent().getGuardianCheck())
                    .guardianGauge(character.getCharacterDayContent().getGuardianGauge())
                    .build();

            // 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산(1415 이상)
            if (character.getItemLevel() >= 1415) {
                Map<Category, DayContent> contentMap = getDayContentByLevel(characterResponseDtoV1.getItemLevel());
                calculateDayContent(characterResponseDtoV1, contentMap, contentResource);
            }
            characterResponseDtoV1List.add(characterResponseDtoV1);
        }
        return characterResponseDtoV1List;
    }



    /**
     * 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
     */
    public CharacterResponseDtoV1 calculateDayContentOne(Character character, Map<String, MarketContentResourceDtoV1> contentResource) {
        // character 엔티티로 dto 객체 생성
        CharacterResponseDtoV1 characterResponseDtoV1 = new CharacterResponseDtoV1(character);

        // 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
        Map<Category, DayContent> contentMap = getDayContentByLevel(characterResponseDtoV1.getItemLevel());
        CharacterResponseDtoV1 returnDto = calculateDayContent(characterResponseDtoV1, contentMap, contentResource);
        return returnDto;
    }

    private CharacterResponseDtoV1 calculateDayContent(CharacterResponseDtoV1 characterResponseDtoV1,
                                                       Map<Category, DayContent> contentMap,
                                                       Map<String , MarketContentResourceDtoV1> contentResource) {
        MarketContentResourceDtoV1 destruction = null;
        MarketContentResourceDtoV1 guardian = null;
        MarketContentResourceDtoV1 leapStone = null;
        if (characterResponseDtoV1.getItemLevel() >= 1415) {
            destruction = contentResource.get("파괴석 결정");
            guardian = contentResource.get("수호석 결정");
            leapStone = contentResource.get("위대한 명예의 돌파석");
        }
        if (characterResponseDtoV1.getItemLevel() >= 1540) {
            destruction = contentResource.get("파괴강석");
            guardian = contentResource.get("수호강석");
            leapStone = contentResource.get("경이로운 명예의 돌파석");
        }
        if (characterResponseDtoV1.getItemLevel() >= 1580) {
            destruction = contentResource.get("정제된 파괴강석");
            guardian = contentResource.get("정제된 수호강석");
            leapStone = contentResource.get("찬란한 명예의 돌파석");
        }
        MarketContentResourceDtoV1 jewelry = contentResource.get("1레벨");
        calculateChaos(characterResponseDtoV1, destruction, guardian, jewelry, contentMap.get(Category.카오스던전));
        calculateGuardian(characterResponseDtoV1, destruction, guardian, leapStone, contentMap.get(Category.가디언토벌));
        return characterResponseDtoV1;
    }


    public void calculateChaos(CharacterResponseDtoV1 characterResponseDtoV1,
                               MarketContentResourceDtoV1 destruction,
                               MarketContentResourceDtoV1 guardian,
                               MarketContentResourceDtoV1 jewelry,
                               DayContent dayContent) {
        double price = 0;
        if (characterResponseDtoV1.getChaosGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(jewelry, dayContent.getJewelry(), price);
                price += dayContent.getGold();
            }
        } else if (characterResponseDtoV1.getChaosGauge() < 40 && characterResponseDtoV1.getChaosGauge() >= 20) {
            for (int i = 0; i < 3; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(jewelry, dayContent.getJewelry(), price);
                price += dayContent.getGold();
            }
        } else {
            for (int i = 0; i < 2; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(jewelry, dayContent.getJewelry(), price);
                price += dayContent.getGold();
            }
        }
        characterResponseDtoV1.setChaosName(dayContent.getName());
        characterResponseDtoV1.setChaosProfit(price);
    }

    private void calculateGuardian(CharacterResponseDtoV1 characterResponseDtoV1,
                                   MarketContentResourceDtoV1 destruction,
                                   MarketContentResourceDtoV1 guardian,
                                   MarketContentResourceDtoV1 leapStone,
                                   DayContent dayContent) {
        double price = 0;
        if (characterResponseDtoV1.getGuardianGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(leapStone, dayContent.getLeapStone(), price);
            }
        } else if (characterResponseDtoV1.getGuardianGauge() < 40 && characterResponseDtoV1.getGuardianGauge() >= 20) {
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
        characterResponseDtoV1.setGuardianName(dayContent.getName());
        characterResponseDtoV1.setGuardianProfit(price);
    }

    /**
     * 번들(묶음) 계산
     */
    private double calculateBundle(MarketContentResourceDtoV1 dto, double count, double price) {
        price += (dto.getRecentPrice() * count) / dto.getBundleCount();
        return Math.round(price * 100.0) / 100.0;
    }


    /**
     * 수익순으로 내림차순 정렬 메소드
     */
    public List<SortedDayContentProfitDtoV1> sortDayContentProfit(List<CharacterResponseDtoV1> characterResponseDtoV1List) {
        Map<DayContentProfitDtoV1, Double> result = new HashMap<>();
        for (CharacterResponseDtoV1 returnDto : characterResponseDtoV1List) {
            if (returnDto.isChaosSelected()) {
                DayContentProfitDtoV1 chaos = new DayContentProfitDtoV1(returnDto.getCharacterName(), "카오스던전",returnDto.getChaosName(), returnDto.getChaosCheck());
                double chaosProfit = returnDto.getChaosProfit();
                result.put(chaos, chaosProfit);
            }

            if (returnDto.isGuardianSelected()) {
                DayContentProfitDtoV1 guardian = new DayContentProfitDtoV1(returnDto.getCharacterName(), "가디언토벌", returnDto.getGuardianName(), returnDto.getGuardianCheck());
                double guardianProfit = returnDto.getGuardianProfit();
                result.put(guardian, guardianProfit);
            }
        }
        List<DayContentProfitDtoV1> listKeySet = new ArrayList<>(result.keySet());
        Collections.sort(listKeySet, (value1, value2) -> (result.get(value2).compareTo(result.get(value1))));
        List<SortedDayContentProfitDtoV1> dtoList = new ArrayList<>();
        for(DayContentProfitDtoV1 key : listKeySet) {
            SortedDayContentProfitDtoV1 dto = new SortedDayContentProfitDtoV1();
            dto.setCharacterName(key.getCharacterName());
            dto.setCategory(key.getCategory());
            dto.setContentName(key.getContentName());
            dto.setChecked(key.getChecked());
            dto.setProfit(result.get(key));
            dtoList.add(dto);
        }
        return dtoList;
    }

}
