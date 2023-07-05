package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.MemberRepository;
import lostark.todo.service.MemberService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final MemberService memberService;
    private final CharacterRepository characterRepository;

    /**
     * 캐릭터 이름으로 같은 계정 캐릭터 데이터 가져와서 저장
     * select은 디폴트 false
    */
    public Member characterInfoAndSave(String username, String characterName) throws Exception {
        Member member = memberService.findUser(username);

        String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
        InputStreamReader inputStreamReader = apiService.LostarkGetApi(link, member.getApiKey());
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);

        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            Character character = new Character(jsonObject);
            member.addCharacter(character);
        }
        return member;
    }

}
