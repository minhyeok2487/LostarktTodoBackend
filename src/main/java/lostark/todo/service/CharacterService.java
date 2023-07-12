package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.characterDto.CharacterRequestDto;
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

    /**
     * 동일한 캐릭터 이름의 데이터 수정
     * @return
     */
    public Character updateCharacter(CharacterRequestDto characterRequestDto) {
        Character character = characterRepository.findByCharacterName(characterRequestDto.getCharacterName());
        character.getCharacterContent().update(characterRequestDto);
        return character;
    }

    public CharacterReturnDto changeContent(DayContentCountDto dto) {
        Character character = characterRepository.findByCharacterName(dto.getCharacterName());
        character.getCharacterContent().changeCount(dto.getCategory());
        CharacterReturnDto characterReturnDto = new CharacterReturnDto(character);
        return characterReturnDto;
    }
}
