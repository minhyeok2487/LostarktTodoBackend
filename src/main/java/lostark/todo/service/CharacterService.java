package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterReturnDto;
import lostark.todo.controller.dto.characterDto.CharacterRequestDto;
import lostark.todo.controller.dto.characterDto.DayContentSelectedDto;
import lostark.todo.controller.dto.characterDto.DayContentSelectedReturnDto;
import lostark.todo.controller.dto.contentDto.DayContentCountDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.member.Member;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;

    /**
     * 동일한 캐릭터 이름의 데이터 수정
     */
    public Character updateCharacter(CharacterRequestDto characterRequestDto) {
        Character character = characterRepository.findByCharacterName(characterRequestDto.getCharacterName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캐릭터입니다."));
        character.getCharacterContent().update(characterRequestDto);
        return character;
    }

    public CharacterReturnDto updateDayContentCheck(DayContentCountDto dto) {
        Character character = characterRepository.findByCharacterName(dto.getCharacterName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캐릭터입니다."));
        character.getCharacterContent().changeCount(dto.getCategory());
        CharacterReturnDto characterReturnDto = new CharacterReturnDto(character);
        return characterReturnDto;
    }

    /**
     * 일일컨텐츠 셀렉트 출력과 변경
     */
    public CharacterReturnDto updateSelected(DayContentSelectedDto dto, String characterName) {
        Character character = characterRepository.findByCharacterName(characterName)
                .orElseThrow(() -> new IllegalArgumentException(characterName + "은(는) 존재하지 않는 캐릭터입니다."));
        character.getCharacterContent().changeSelected(dto);
        return new CharacterReturnDto(character);
    }

    public DayContentSelectedReturnDto readSelected(String characterName) {
        Character character = characterRepository.findByCharacterName(characterName)
                .orElseThrow(() -> new IllegalArgumentException(characterName + "은(는) 존재하지 않는 캐릭터입니다."));
        return new DayContentSelectedReturnDto(character);
    }

    public List<CharacterReturnDto> saveCharacterList(Member member, JSONArray characterList) {
        List<CharacterReturnDto> returnDtos = new ArrayList<>();
        for (Object o : characterList) {
            JSONObject jsonObject = (JSONObject) o;
            Character character = new Character(jsonObject);
            Character savedCharacter = member.addCharacter(character); // 데이터 저장
            CharacterReturnDto dto = new CharacterReturnDto(savedCharacter); // 저장된 데이터 리턴 dto로 변경
            returnDtos.add(dto);
        }
        return returnDtos;
    }

    public void updateCharacterList(JSONArray characterList) {
        for (Object o : characterList) {
            JSONObject jsonObject = (JSONObject) o;
            String characterName = jsonObject.get("CharacterName").toString();
            double itemLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", ""));
            characterRepository.updateCharacterInfo(characterName, itemLevel);
        }
    }
}
