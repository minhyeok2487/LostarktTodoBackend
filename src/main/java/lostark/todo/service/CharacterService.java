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
}
