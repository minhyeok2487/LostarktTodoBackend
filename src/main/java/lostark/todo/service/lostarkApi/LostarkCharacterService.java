package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkCharacterService {

    private final LostarkApiService apiService;
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
            JSONArray result = getCharacterImage(filteredArray, apiKey);
            return result;
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

    // 캐릭터 imageUrl 가져오기
    private JSONArray getCharacterImage(JSONArray jsonArray, String apiKey) {
        JSONArray result = new JSONArray();
        for (Object obj : jsonArray) {
            try {
                JSONObject jsonObject = (JSONObject) obj;
                String characterName = jsonObject.get("CharacterName").toString();
                String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
                String link = "https://developer-lostark.game.onstove.com/armories/characters/"+encodeCharacterName+"/profiles";
                InputStreamReader inputStreamReader = apiService.LostarkGetApi(link, apiKey);
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
