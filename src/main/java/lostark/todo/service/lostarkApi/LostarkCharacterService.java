package lostark.todo.service.lostarkApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.character.Settings;
import lostark.todo.domain.content.DayContent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkCharacterService {

    private final LostarkApiService apiService;

    /**
     * 대표캐릭터와 연동된 캐릭터 호출(api 검증)
     * @param characterName
     * @param apiKey
     * @param chaos
     * @param guardian
     * @return
     */
    public List<Character> findCharacterList(String characterName, String apiKey, List<DayContent> chaos, List<DayContent> guardian) {
        try {
            JSONArray jsonArray = findCharacters(characterName, apiKey);
            // 1415이상만 필터링
            JSONArray filteredArray = filterLevel(jsonArray);

            JSONArray imageList = getCharacterImage(filteredArray,apiKey);
            List<Character> characterList = new ArrayList<>();
            for (Object o : imageList) {
                JSONObject jsonObject = (JSONObject) o;

                Character character = Character.builder()
                        .characterName(jsonObject.get("CharacterName").toString())
                        .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
                        .characterClassName(jsonObject.get("CharacterClassName").toString())
                        .serverName(jsonObject.get("ServerName").toString())
                        .itemLevel(Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", "")))
                        .dayTodo(new DayTodo())
                        .build();
                character.setSettings(new Settings());
                character.setTodoList(new ArrayList<>());
                character.setTodoV2List(new ArrayList<>());
                character.createImage(jsonObject.get("CharacterImage"));
                character.getDayTodo().createDayContent(chaos, guardian, character.getItemLevel());
                characterList.add(character);
            }
            //레벨순으로 정렬 후 리턴
            AtomicInteger sortNumber = new AtomicInteger();
            List<Character> sortedList = characterList.stream()
                    .sorted(Comparator.comparing(Character::getItemLevel).reversed()).collect(Collectors.toList())
                    .stream().map(character -> {
                        character.setSortNumber(sortNumber.getAndIncrement());
                        return character;
                    })
                    .collect(Collectors.toList());
            return sortedList;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(characterName + " 은(는) 존재하지 않는 캐릭터 입니다.");
        } catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 캐릭터 리스트 출력
     */
    public JSONArray findCharacters(String characterName, String apiKey) throws IOException, ParseException {
        String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
        InputStreamReader inputStreamReader = apiService.lostarkGetApi(link, apiKey);
        JSONParser parser = new JSONParser();
        return (JSONArray) parser.parse(inputStreamReader);
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

                if (profile.get("CharacterImage") != null) {
                    jsonObject.put("CharacterImage", profile.get("CharacterImage").toString());
                }
                result.add(jsonObject);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public JSONObject findCharacter(String characterName, String apiKey) {
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/"+encodeCharacterName+"/profiles";
            InputStreamReader inputStreamReader = apiService.lostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(inputStreamReader);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
