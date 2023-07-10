package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.characterDto.CharacterSaveDto;
import lostark.todo.controller.dto.contentDto.DayContentCountDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Character character = characterRepository.findByCharacterName(characterSaveDto.getCharacterName());
        character.update(characterSaveDto);
        return character;
    }

    public CharacterReturnDto changeContent(DayContentCountDto dto) {
        Character character = characterRepository.findByCharacterName(dto.getCharacterName());
        character.changeCount(dto.getCategory());
        CharacterReturnDto characterReturnDto = new CharacterReturnDto(character);
        return characterReturnDto;
    }
}
