package lostark.todo.service.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterDayContent;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.member.Member;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

}
