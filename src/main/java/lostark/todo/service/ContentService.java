package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dto.DayContentDto;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.contentDto.DayContentProfitDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final DayContentRepository dayContentRepository;
    private final ContentRepository contentRepository;

    public Content findContentById(Long id) {
        return contentRepository.findById(id).orElseThrow();
    }

    public DayContent saveDayContent(DayContent dayContent, int level, String name) {
        dayContent.setCategory(Category.일일);
        dayContent.setLevel(level);
        dayContent.setName(name);
        DayContent saved = dayContentRepository.save(dayContent);
        return saved;
    }

    public DayContent findDayContentById(Long id) {
        return dayContentRepository.findById(id).orElseThrow();
    }

    public List<DayContent> findDayContents() {
        return dayContentRepository.findAll();
    }


    public DayContent updateDayContent(DayContentDto dayContentDto) {
        DayContent dayContent = findDayContentById(dayContentDto.getId());
        DayContent updated = dayContent.update(dayContentDto);
        return updated;
    }

    public Map<Category, DayContent> getDayContentByLevel(double level) {
        DayContent chaosContent = contentRepository.findDayContentByLevel(level, Category.카오스던전).get(0);
        DayContent guardianContent = contentRepository.findDayContentByLevel(level, Category.가디언토벌).get(0);

        Map<Category, DayContent> dayContentMap = new HashMap<>();
        dayContentMap.put(Category.카오스던전, chaosContent);
        dayContentMap.put(Category.가디언토벌, guardianContent);
        return dayContentMap;
    }

    public CharacterReturnDto calculateDayContentOne(Character character, Map<String, MarketContentResourceDto> contentResource) {
        // character 엔티티로 dto 객체 생성
        CharacterReturnDto characterReturnDto = new CharacterReturnDto(character);

        // 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
        Map<Category, DayContent> contentMap = getDayContentByLevel(characterReturnDto.getItemLevel());
        CharacterReturnDto returnDto = calculateDayContent(characterReturnDto, contentMap, contentResource);
        return returnDto;
    }

    public List<CharacterReturnDto> calculateDayContent(
            List<Character> characterList, Map<String, MarketContentResourceDto> contentResource) {
        List<CharacterReturnDto> characterReturnDtoList = new ArrayList<>(); //출력할 리스트

        for (Character character : characterList) {
            // character 엔티티로 dto 객체 생성
            CharacterReturnDto characterReturnDto = new CharacterReturnDto(character);

            // 객체 레벨에 맞는 일일 컨텐츠 가져온후 계산
            Map<Category, DayContent> contentMap = getDayContentByLevel(characterReturnDto.getItemLevel());
            calculateDayContent(characterReturnDto, contentMap, contentResource);
            characterReturnDtoList.add(characterReturnDto);
        }
        return characterReturnDtoList;
    }

    private CharacterReturnDto calculateDayContent(CharacterReturnDto characterReturnDto,
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
        MarketContentResourceDto jewelry = contentResource.get("1레벨");
        calculateChaos(characterReturnDto, destruction, guardian, jewelry, contentMap.get(Category.카오스던전));
        calculateGuardian(characterReturnDto, destruction, guardian, leapStone, contentMap.get(Category.가디언토벌));
        return characterReturnDto;
    }


    public void calculateChaos(CharacterReturnDto characterReturnDto,
                               MarketContentResourceDto destruction,
                               MarketContentResourceDto guardian,
                               MarketContentResourceDto jewelry,
                               DayContent dayContent) {
        double price = 0;
        if (characterReturnDto.getChaosGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, dayContent.getDestructionStone(), price);
                price = calculateBundle(guardian, dayContent.getGuardianStone(), price);
                price = calculateBundle(jewelry, dayContent.getJewelry(), price);
                price += dayContent.getGold();
            }
        } else if (characterReturnDto.getChaosGauge() < 40 && characterReturnDto.getChaosGauge() >= 20) {
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

    public JSONArray sortDayContentProfit(List<CharacterReturnDto> characterReturnDtoList) {
        Map<DayContentProfitDto, Double> result = new HashMap<>();
        for (CharacterReturnDto returnDto : characterReturnDtoList) {
            DayContentProfitDto guardian = new DayContentProfitDto(returnDto.getCharacterName(), Category.가디언토벌, returnDto.getGuardianName(), returnDto.getGuardian());
            double guardianProfit = returnDto.getGuardianProfit();
            result.put(guardian, guardianProfit);

            DayContentProfitDto chaos = new DayContentProfitDto(returnDto.getCharacterName(), Category.카오스던전,returnDto.getChaosName(), returnDto.getChaos());
            double chaosProfit = returnDto.getChaosProfit();
            result.put(chaos, chaosProfit);
        }
        List<DayContentProfitDto> listKeySet = new ArrayList<>(result.keySet());
        JSONArray jsonArray = new JSONArray();
        Collections.sort(listKeySet, (value1, value2) -> (result.get(value2).compareTo(result.get(value1))));
        for(DayContentProfitDto key : listKeySet) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("characterName", key.getCharacterName());
            jsonObject.put("category", key.getCategory());
            jsonObject.put("contentName", key.getContentName());
            jsonObject.put("checked", key.getChecked());
            jsonObject.put("profit", result.get(key));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

}
