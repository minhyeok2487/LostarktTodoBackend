package lostark.todo.domain.character;

import lostark.todo.domain.member.Member;
import lostark.todo.service.lostarkApi.LostarkApiService;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class CharacterTest {

    @Autowired
    private LostarkApiService apiService;

    @Value("${Lostark-API-Key}")
    private String lostarkApiKey;

    private String characterName = "마볼링";

    @Test
    void 엔티티테스트_캐릭터저장테스트_JSONObject() {
        /**
         * 주로 디폴트 true가 잘 생성됬는지 테스트
         * 로스트아크 api로 불러온 캐릭터 엔티티가 생성될때 default값으로
         * selected = true이고
         * characterContent = (true, 0, 0, true, 0, 0) 이다
         */
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
            InputStreamReader inputStreamReader = apiService.LostarkGetApi(link, lostarkApiKey);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);

            JSONObject jsonObject = (JSONObject) jsonArray.get(2);
            Character character = new Character(jsonObject);

            assertThat(character.isSelected()).isTrue();
            assertThat(character.getCharacterContent().isChaosSelected()).isTrue();
            assertThat(character.getCharacterContent().isGuardianSelected()).isTrue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}