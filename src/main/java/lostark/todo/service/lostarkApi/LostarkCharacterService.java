package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.MemberSignupDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterDayContent;
import lostark.todo.service.lostarkApi.LostarkApiService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkCharacterService {

    private final LostarkApiService apiService;


    public List<Character> getCharacterList(MemberSignupDto signupDto) {
        try {
            String encodeCharacterName = URLEncoder.encode(signupDto.getCharacterName(), StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
            InputStreamReader inputStreamReader = apiService.lostarkGetApi(link, signupDto.getApiKey());
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);

            JSONArray filteredArray = filterLevel(jsonArray);


            JSONArray imageList = getCharacterImage(filteredArray, signupDto.getApiKey());
            List<Character> characterList = new ArrayList<>();
            for (Object o : imageList) {
                JSONObject jsonObject = (JSONObject) o;
                Character character = Character.builder()
                        .characterName(jsonObject.get("CharacterName").toString())
                        .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
                        .characterClassName(jsonObject.get("CharacterClassName").toString())
                        .serverName(jsonObject.get("ServerName").toString())
                        .itemLevel(Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", "")))
                        .characterImage(jsonObject.get("CharacterImage").toString())
                        .characterDayContent(new CharacterDayContent())
                        .build();
                characterList.add(character);
            }
            return characterList;
        } catch (NullPointerException e) {
            throw new RuntimeException(signupDto.getCharacterName() + " 은(는) 존재하지 않는 캐릭터 입니다.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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
        if (filteredArray.isEmpty()) {
            throw new RuntimeException("아이템 레벨 1415 이상 캐릭터가 없습니다.");
        }
        return filteredArray;
    }

    // 캐릭터 imageUrl 가져오기
    private JSONArray getCharacterImage(JSONArray jsonArray, String apiKey) {
        JSONArray result = new JSONArray();
        for (Object obj : jsonArray) {
            try {
                JSONObject jsonObject = (JSONObject) obj;
                String characterName = jsonObject.get("CharacterName").toString();
                String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
                String link = "https://developer-lostark.game.onstove.com/armories/characters/"+encodeCharacterName+"/profiles";
                InputStreamReader inputStreamReader = apiService.lostarkGetApi(link, apiKey);
                JSONParser parser = new JSONParser();
                JSONObject profile = (JSONObject) parser.parse(inputStreamReader);

                jsonObject.put("CharacterImage", profile.get("CharacterImage").toString());
                result.add(jsonObject);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
