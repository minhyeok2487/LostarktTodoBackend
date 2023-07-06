package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.characterDto.CharacterSaveDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;

    public Character findCharacterByName(String characterName) {
        return characterRepository.findByCharacterName(characterName);
    }

    public Character saveCharacter(CharacterSaveDto characterSaveDto) {
        Character character = characterRepository.findById(characterSaveDto.getId()).orElseThrow();
        character.update(characterSaveDto);
        return character;
    }

    public CharacterReturnDto calculateDayContent(CharacterReturnDto characterReturnDto, Market destruction, Market guardian, Market leapStone, DayContent dayContent) {
        double price = 0;
        if (characterReturnDto.getChaosGauge() >= 40) {
            for (int i = 0; i < 4; i++) {
                price = calChaos(destruction, guardian, leapStone, dayContent, price);
            }
        } else if (characterReturnDto.getChaosGauge() < 40 && characterReturnDto.getChaosGauge() >= 20) {
            for (int i = 0; i < 3; i++) {
                price = calChaos(destruction, guardian, leapStone, dayContent, price);
            }
        } else {
            for (int i = 0; i < 2; i++) {
                price = calChaos(destruction, guardian, leapStone, dayContent, price);
            }
        }
        price += dayContent.getGold();
        characterReturnDto.setChaosName(dayContent.getName());
        characterReturnDto.setChaosProfit(price);
        return characterReturnDto;
    }

    private double calChaos(Market destruction, Market guardian, Market leapStone, DayContent dayContent, double price) {
        price += (destruction.getRecentPrice() * dayContent.getDestructionStone()) / destruction.getBundleCount();
        price += (guardian.getRecentPrice() * dayContent.getGuardianStone()) / guardian.getBundleCount();
        price += (leapStone.getRecentPrice() * dayContent.getLeapStone()) / leapStone.getBundleCount();
        return Math.round(price * 100.0) / 100.0;
    }
}
