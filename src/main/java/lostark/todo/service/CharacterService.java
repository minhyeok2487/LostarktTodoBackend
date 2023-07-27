package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterDayContentResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.content.Category;
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
     * 캐릭터 조회
     */
    public Character findCharacter(String characterName) {
        return characterRepository.findByCharacterName(characterName)
                .orElseThrow(() -> new IllegalArgumentException(characterName+" 은(는) 존재하지 않는 캐릭터입니다."));
    }

    /**
     * 캐릭터 수정
     */
    public Character updateCharacter(String characterName, CharacterDayContentResponseDto characterDayContentResponseDto) {
        Character character = findCharacter(characterName);
        character.getCharacterDayContent().update(characterDayContentResponseDto);
        return character;
    }

    /**
     * 일일컨텐츠 체크 일괄 수정
     * 1수보다 작으면 -> 2수
     * 2수 -> 0수
     */
    public Character updateDayContentCheck(String characterName, Category category) {
        Character character = findCharacter(characterName);
        character.getCharacterDayContent().changeCount(category);
        return character;
    }

    /**
     * 일일컨텐츠 셀렉트 변경
     * true -> 할 컨텐츠
     * false -> 안할 컨텐츠
     */
    public Character updateDayContentSelected(String characterName, Category category) {
        Character character = findCharacter(characterName);
        character.getCharacterDayContent().changeSelected(category);
        return character;
    }


    /**
     * 로스트아크 api로 불러온 캐릭터 리스트 DB 저장
     */
//    public String saveCharacterList(Member member, JSONArray characterList) {
//        int number = 0;
//        String message = "";
//        for (Object o : characterList) {
//            JSONObject jsonObject = (JSONObject) o;
//            Character character = new Character(jsonObject);
//            member.addCharacter(character); // 데이터 저장
//            number++;
//            message += character.getCharacterName() + " ";
//        }
//        String result = "총 " + number +"개 저장 완료\n" + message;
//        return result;
//    }

    /**
     * 로스트아크 api로 불러온 캐릭터 리스트 DB 갱신
     */
    public String updateCharacterList(JSONArray characterList) {
        int number = 0;
        String message = "";
        for (Object o : characterList) {
            JSONObject jsonObject = (JSONObject) o;
            String characterName = jsonObject.get("CharacterName").toString();
            double itemLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", ""));
            characterRepository.updateCharacterInfo(characterName, itemLevel);
            number++;
            message += characterName + " ";
        }
        String result = "총 " + number +"개 업데이트 완료\n" + message;
        return result;
    }
}
