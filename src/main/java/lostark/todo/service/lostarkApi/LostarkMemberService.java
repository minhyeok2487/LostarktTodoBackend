package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
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
public class LostarkMemberService {

    private final LostarkApiService apiService;
    private final MemberRepository memberRepository;
    /**
     * 캐릭터 이름으로 같은 계정 캐릭터 데이터 가져와서 저장
     * select은 디폴트 true
     * 1415이상 캐릭터만 가져옴
    */
    public Member characterInfoAndSave(String username, String characterName)  {
        try {
            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException(username+"은(는) 없는 회원 입니다."));

            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
            InputStreamReader inputStreamReader = apiService.LostarkGetApi(link, member.getApiKey());
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                if (Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",","")) >= 1415D) {
                    Character character = new Character(jsonObject);
                    member.addCharacter(character);
                }
            }
            return member;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MemberResponseDto characterPatch(String username) {
        try {
            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException(username+"은(는) 없는 회원 입니다."));
            List<Character> characters = member.getCharacters();
            System.out.println("characters.get(0).getCharacterName() = " + characters.get(0).getCharacterName());

            String encodeCharacterName = URLEncoder.encode(characters.get(0).getCharacterName(), StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
            InputStreamReader inputStreamReader = apiService.LostarkGetApi(link, member.getApiKey());
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(inputStreamReader);
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                for (Character character : characters) {
                    if(character.getCharacterName().equals(jsonObject.get("CharacterName").toString())) {
                        character.changeItemLevel(Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",","")));
                        break;
                    }
                }
            }
            MemberResponseDto responseDto = new MemberResponseDto(member);
            return responseDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
