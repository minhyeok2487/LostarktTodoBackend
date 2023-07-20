package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.MemberRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkCharacterService {

    private final LostarkApiService apiService;
    private final MemberRepository memberRepository;
    /**
     * 캐릭터 이름으로 같은 계정 캐릭터 데이터 가져옴
     * 1415이상 캐릭터만 리턴
     */
    public JSONArray characterInfo(String apiKey, String characterName) {
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
            InputStreamReader inputStreamReader = apiService.LostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);

            JSONArray filteredArray = filterLevel(jsonArray);
            return filteredArray;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray characterInfoPatch(String apiKey, String characterName) {
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
            InputStreamReader inputStreamReader = apiService.LostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);
            JSONArray filteredArray = filterLevel(jsonArray);
            return filteredArray;
//            for (Object o : jsonArray) {
//                JSONObject jsonObject = (JSONObject) o;
//                for (Character character : characters) {
//                    if(character.getCharacterName().equals(jsonObject.get("CharacterName").toString())) {
//                        character.changeItemLevel(Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",","")));
//                        break;
//                    }
//                }
//            }
//            MemberResponseDto responseDto = new MemberResponseDto(member);
//            return responseDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 1415이상만 필터링 메소드
    private JSONArray filterLevel(JSONArray jsonArray) {
        JSONArray filteredArray = new JSONArray();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            double itemMaxLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", ""));
            if (itemMaxLevel >= 1415D) {
                filteredArray.add(jsonObject);
            }
        }
        return filteredArray;
    }


}
