package lostark.todo.service.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lostark.todo.controller.v1.dto.marketDto.MarketContentResourceDto;
import lostark.todo.controller.v2.dto.characterDto.CharacterResponseDtoV2;
import lostark.todo.controller.v2.dto.marketDto.MarketContentResourceDtoV2;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterDayContent;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.member.Member;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterServiceV2 {

    private final CharacterRepository characterRepository;

    /**
     * 캐릭터 조회
     */
    public Character findCharacter(String characterName) {
        return characterRepository.findByCharacterName(characterName)
                .orElseThrow(() -> new IllegalArgumentException(characterName+" 은(는) 존재하지 않는 캐릭터입니다."));
    }


    /**
     * 캐릭터 리스트 저장
     */
    public void saveCharacterList(Member member, JSONArray characterList) {
        for (Object o : characterList) {
            JSONObject jsonObject = (JSONObject) o;
            Character character = Character.builder()
                    .member(member)
                    .characterName(jsonObject.get("CharacterName").toString())
                    .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
                    .characterClassName(jsonObject.get("CharacterClassName").toString())
                    .serverName(jsonObject.get("ServerName").toString())
                    .itemLevel(Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", "")))
                    .characterImage(jsonObject.get("CharacterImage").toString())
                    .characterDayContent(new CharacterDayContent())
                    .build();
            characterRepository.save(character);
        }
    }

    /**
     * 수익 계산
     */
    public void calculateProfit(
            List<CharacterResponseDtoV2> characterResponseDtoList,
            Map<String, MarketContentResourceDtoV2> contentResource) {
        MarketContentResourceDtoV2 destruction = null;
        MarketContentResourceDtoV2 guardian = null;
        MarketContentResourceDtoV2 leapStone = null;
        for (CharacterResponseDtoV2 characterResponseDtoV2 : characterResponseDtoList) {
            if (characterResponseDtoV2.getItemLevel() >= 1415) {
                destruction = contentResource.get("파괴석 결정");
                guardian = contentResource.get("수호석 결정");
                leapStone = contentResource.get("위대한 명예의 돌파석");
            }
            if (characterResponseDtoV2.getItemLevel() >= 1540) {
                destruction = contentResource.get("파괴강석");
                guardian = contentResource.get("수호강석");
                leapStone = contentResource.get("경이로운 명예의 돌파석");
            }
            if (characterResponseDtoV2.getItemLevel() >= 1580) {
                destruction = contentResource.get("정제된 파괴강석");
                guardian = contentResource.get("정제된 수호강석");
                leapStone = contentResource.get("찬란한 명예의 돌파석");
            }
            MarketContentResourceDtoV2 jewelry = contentResource.get("1레벨");
            calculateChaos(characterResponseDtoV2, destruction, guardian, jewelry);
            calculateGuardian(characterResponseDtoV2, destruction, guardian, leapStone);
        }

    }

    public void calculateChaos(CharacterResponseDtoV2 characterResponseDto,
                               MarketContentResourceDtoV2 destruction,
                               MarketContentResourceDtoV2 guardian,
                               MarketContentResourceDtoV2 jewelry) {
        double price = 0;
        if (characterResponseDto.getChaosGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, characterResponseDto.getChaosName().getDestructionStone(), price);
                price = calculateBundle(guardian, characterResponseDto.getChaosName().getGuardianStone(), price);
                price = calculateBundle(jewelry, characterResponseDto.getChaosName().getJewelry(), price);
                price += characterResponseDto.getChaosName().getGold();
            }
        } else if (characterResponseDto.getChaosGauge() < 40 && characterResponseDto.getChaosGauge() >= 20) {
            for (int i = 0; i < 3; i++) {
                price = calculateBundle(destruction, characterResponseDto.getChaosName().getDestructionStone(), price);
                price = calculateBundle(guardian, characterResponseDto.getChaosName().getGuardianStone(), price);
                price = calculateBundle(jewelry, characterResponseDto.getChaosName().getJewelry(), price);
                price += characterResponseDto.getChaosName().getGold();
            }
        } else {
            for (int i = 0; i < 2; i++) {
                price = calculateBundle(destruction, characterResponseDto.getChaosName().getDestructionStone(), price);
                price = calculateBundle(guardian, characterResponseDto.getChaosName().getGuardianStone(), price);
                price = calculateBundle(jewelry, characterResponseDto.getChaosName().getJewelry(), price);
                price += characterResponseDto.getChaosName().getGold();
            }
        }
        characterResponseDto.setChaosProfit(price);
    }

    private void calculateGuardian(CharacterResponseDtoV2 characterResponseDto,
                                   MarketContentResourceDtoV2 destruction,
                                   MarketContentResourceDtoV2 guardian,
                                   MarketContentResourceDtoV2 leapStone) {
        double price = 0;
        if (characterResponseDto.getGuardianGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calculateBundle(destruction, characterResponseDto.getGuardianName().getDestructionStone(), price);
                price = calculateBundle(guardian, characterResponseDto.getGuardianName().getGuardianStone(), price);
                price = calculateBundle(leapStone, characterResponseDto.getGuardianName().getLeapStone(), price);
            }
        } else if (characterResponseDto.getGuardianGauge() < 40 && characterResponseDto.getGuardianGauge() >= 20) {
            for (int i = 0; i < 3; i++) {
                price = calculateBundle(destruction, characterResponseDto.getGuardianName().getDestructionStone(), price);
                price = calculateBundle(guardian, characterResponseDto.getGuardianName().getGuardianStone(), price);
                price = calculateBundle(leapStone, characterResponseDto.getGuardianName().getLeapStone(), price);
            }
        } else {
            for (int i = 0; i < 2; i++) {
                price = calculateBundle(destruction, characterResponseDto.getGuardianName().getDestructionStone(), price);
                price = calculateBundle(guardian, characterResponseDto.getGuardianName().getGuardianStone(), price);
                price = calculateBundle(leapStone, characterResponseDto.getGuardianName().getLeapStone(), price);
            }
        }
        characterResponseDto.setGuardianProfit(price);
    }


    /**
     * 번들(묶음) 계산
     */
    private double calculateBundle(MarketContentResourceDtoV2 dto, double count, double price) {
        price += (dto.getRecentPrice() * count) / dto.getBundleCount();
        return Math.round(price * 100.0) / 100.0;
    }
}
