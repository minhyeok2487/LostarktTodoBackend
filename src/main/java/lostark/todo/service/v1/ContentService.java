package lostark.todo.service.v1;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDtoV2;
import lostark.todo.controller.v1.dto.contentDto.DayContentProfitDto;
import lostark.todo.controller.v1.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.v1.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;

    public List<CharacterResponseDtoV2> calculateDayContentV2(List<Character> characterList) {
        //출력할 리스트
        List<CharacterResponseDtoV2> characterResponseDtoList = new ArrayList<>();

        for (Character character : characterList) {
            // 캐릭터 레벨에 따른 일일컨텐츠
            Map<Category, DayContent> contentMap = getDayContentByLevel(character.getItemLevel());

            // character 엔티티로 dto 객체 생성
            CharacterResponseDtoV2 characterResponseDto = CharacterResponseDtoV2.builder()
                    .id(character.getId())
                    .characterName(character.getCharacterName())
                    .characterImage(character.getCharacterImage())
                    .characterClassName(character.getCharacterClassName())
                    .itemLevel(character.getItemLevel())
                    .chaosSelected(character.getCharacterDayContent().isChaosSelected())
                    .chaosCheck(character.getCharacterDayContent().getChaosCheck())
                    .chaosGauge(character.getCharacterDayContent().getChaosGauge())
                    .chaosName(contentMap.get(Category.카오스던전))
                    .guardianSelected(character.getCharacterDayContent().isGuardianSelected())
                    .guardianCheck(character.getCharacterDayContent().getGuardianCheck())
                    .guardianGauge(character.getCharacterDayContent().getGuardianGauge())
                    .guardianName(contentMap.get(Category.가디언토벌))
                    .build();

            characterResponseDtoList.add(characterResponseDto);
            }
        return characterResponseDtoList;
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

    public List<CharacterResponseDto> calculateDayContent(
            List<Character> characterList, Map<String, MarketContentResourceDto> contentResource) {
        List<CharacterResponseDto> characterResponseDtoList = new ArrayList<>(); //출력할 리스트

        for (Character character : characterList) {
            // character 엔티티로 dto 객체 생성
            CharacterResponseDto characterResponseDto = CharacterResponseDto.builder()
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
                Map<Category, DayContent> contentMap = getDayContentByLevel(characterResponseDto.getItemLevel());
                calculateDayContent(characterResponseDto, contentMap, contentResource);
            }
            characterResponseDtoList.add(characterResponseDto);
        }
        return characterResponseDtoList;
    }



    /**
     * 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
     */
    public CharacterResponseDto calculateDayContentOne(Character character, Map<String, MarketContentResourceDto> contentResource) {
        // character 엔티티로 dto 객체 생성
        CharacterResponseDto characterResponseDto = new CharacterResponseDto(character);

        // 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
        Map<Category, DayContent> contentMap = getDayContentByLevel(characterResponseDto.getItemLevel());
        CharacterResponseDto returnDto = calculateDayContent(characterResponseDto, contentMap, contentResource);
        return returnDto;
    }

    private CharacterResponseDto calculateDayContent(CharacterResponseDto characterResponseDto,
                                                     Map<Category, DayContent> contentMap,
                                                     Map<String , MarketContentResourceDto> contentResource) {
        MarketContentResourceDto destruction = null;
        MarketContentResourceDto guardian = null;
        MarketContentResourceDto leapStone = null;
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
        MarketContentResourceDto jewelry = contentResource.get("1레벨");
        calculateChaos(characterResponseDto, destruction, guardian, jewelry, contentMap.get(Category.카오스던전));
        calculateGuardian(characterResponseDto, destruction, guardian, leapStone, contentMap.get(Category.가디언토벌));
        return characterResponseDto;
    }


    public void calculateChaos(CharacterResponseDto characterResponseDto,
                               MarketContentResourceDto destruction,
                               MarketContentResourceDto guardian,
                               MarketContentResourceDto jewelry,
                               DayContent dayContent) {
        double price = 0;
        if (characterResponseDto.getChaosGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(jewelry, dayContent.getJewelry(), price);
                price += dayContent.getGold();
            }
        } else if (characterResponseDto.getChaosGauge() < 40 && characterResponseDto.getChaosGauge() >= 20) {
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
        characterResponseDto.setChaosName(dayContent.getName());
        characterResponseDto.setChaosProfit(price);
    }

    private void calculateGuardian(CharacterResponseDto characterResponseDto,
                                   MarketContentResourceDto destruction,
                                   MarketContentResourceDto guardian,
                                   MarketContentResourceDto leapStone,
                                   DayContent dayContent) {
        double price = 0;
        if (characterResponseDto.getGuardianGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(leapStone, dayContent.getLeapStone(), price);
            }
        } else if (characterResponseDto.getGuardianGauge() < 40 && characterResponseDto.getGuardianGauge() >= 20) {
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
        characterResponseDto.setGuardianName(dayContent.getName());
        characterResponseDto.setGuardianProfit(price);
    }

    /**
     * 번들(묶음) 계산
     */
    private double calculateBundle(MarketContentResourceDto dto, double count, double price) {
        price += (dto.getRecentPrice() * count) / dto.getBundleCount();
        return Math.round(price * 100.0) / 100.0;
    }


    /**
     * 수익순으로 내림차순 정렬 메소드
     */
    public List<SortedDayContentProfitDto> sortDayContentProfit(List<CharacterResponseDto> characterResponseDtoList) {
        Map<DayContentProfitDto, Double> result = new HashMap<>();
        for (CharacterResponseDto returnDto : characterResponseDtoList) {
            if (returnDto.isChaosSelected()) {
                DayContentProfitDto chaos = new DayContentProfitDto(returnDto.getCharacterName(), "카오스던전",returnDto.getChaosName(), returnDto.getChaosCheck());
                double chaosProfit = returnDto.getChaosProfit();
                result.put(chaos, chaosProfit);
            }

            if (returnDto.isGuardianSelected()) {
                DayContentProfitDto guardian = new DayContentProfitDto(returnDto.getCharacterName(), "가디언토벌", returnDto.getGuardianName(), returnDto.getGuardianCheck());
                double guardianProfit = returnDto.getGuardianProfit();
                result.put(guardian, guardianProfit);
            }
        }
        List<DayContentProfitDto> listKeySet = new ArrayList<>(result.keySet());
        Collections.sort(listKeySet, (value1, value2) -> (result.get(value2).compareTo(result.get(value1))));
        List<SortedDayContentProfitDto> dtoList = new ArrayList<>();
        for(DayContentProfitDto key : listKeySet) {
            SortedDayContentProfitDto dto = new SortedDayContentProfitDto();
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
