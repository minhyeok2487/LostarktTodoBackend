package lostark.todo.domain.lostark.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.character.CharacterJsonDto;
import lostark.todo.domain.util.content.enums.Category;
import lostark.todo.domain.util.content.repository.ContentRepository;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.DayTodo;
import lostark.todo.domain.character.entity.Settings;
import lostark.todo.domain.character.entity.WeekTodo;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
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
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LostarkCharacterApiClient {

    private final LostarkApiClient apiClient;
    private final ContentRepository contentRepository;

    /**
     * 대표캐릭터와 연동된 캐릭터 호출(api 검증)
     * @param characterName
     * @param apiKey
     * @return
     */
    public List<Character> createCharacterList(String characterName, String apiKey) {
        try {
            JSONArray jsonArray = findCharacters(characterName, apiKey);

            // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
            Map<Category, List<DayContent>> dayContent = contentRepository.getDayContents();

            List<Character> characterList = new ArrayList<>();
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;

                Character character = Character.builder()
                        .characterName(jsonObject.get("CharacterName").toString())
                        .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
                        .characterClassName(jsonObject.get("CharacterClassName").toString())
                        .serverName(jsonObject.get("ServerName").toString())
                        .itemLevel(Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", "")))
                        .dayTodo(new DayTodo())
                        .weekTodo(new WeekTodo())
                        .build();
                character.setSettings(new Settings());
                character.setTodoV2List(new ArrayList<>());
                character.setCharacterImage(getCharacterImageUrl(character.getCharacterName(), apiKey));
                character.getDayTodo().createDayContent(
                        dayContent.get(Category.카오스던전), dayContent.get(Category.가디언토벌), character.getItemLevel());
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
            throw new ConditionNotMetException("존재하지 않는 캐릭터명 입니다.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 캐릭터 리스트 출력
     */
    public JSONArray findCharacters(String characterName, String apiKey) {
        String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String link = "https://developer-lostark.game.onstove.com/characters/"+encodeCharacterName+"/siblings";
        InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
        JSONParser parser = new JSONParser();
        try {
            JSONArray parse = (JSONArray) parser.parse(inputStreamReader);
            return filterLevel(parse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CharacterJsonDto> getSiblings(String characterName, String apiKey) {
        String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
        String url = "https://developer-lostark.game.onstove.com/characters/" + encodedCharacterName + "/siblings";

        try (InputStreamReader reader = apiClient.lostarkGetApi(url, apiKey)) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<CharacterJsonDto> characterList = objectMapper.readValue(
                    reader,
                    new TypeReference<List<CharacterJsonDto>>() {}
            );

            return characterList.stream()
                    .filter(this::isItemLevelAboveThreshold)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error fetching character list", e);
        }
    }

    private boolean isItemLevelAboveThreshold(CharacterJsonDto dto) {
        try {
            double itemLevel = dto.getItemMaxLevel();
            return itemLevel >= 1415.00;
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage());
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
                InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
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

    public String getCharacterImageUrl(String characterName, String apiKey) {
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/"+encodeCharacterName+"/profiles";
            InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);
            JSONParser parser = new JSONParser();
            JSONObject profile = (JSONObject) parser.parse(inputStreamReader);
            if (profile != null && profile.get("CharacterImage") != null) {
                return profile.get("CharacterImage").toString();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterJsonDto getCharacter(String characterName, String apiKey) {
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodeCharacterName + "/profiles";

            InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStreamReader, CharacterJsonDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterJsonDto getCharacterWithException(String characterName, String apiKey) {
        try {
            String encodeCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String link = "https://developer-lostark.game.onstove.com/armories/characters/" + encodeCharacterName + "/profiles";

            InputStreamReader inputStreamReader = apiClient.lostarkGetApi(link, apiKey);

            ObjectMapper objectMapper = new ObjectMapper();
            CharacterJsonDto characterJsonDto = objectMapper.readValue(inputStreamReader, CharacterJsonDto.class);

            validateCharacter(characterJsonDto);
            return characterJsonDto;
        } catch (ConditionNotMetException e) {
            throw new ConditionNotMetException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void validateCharacter(CharacterJsonDto characterJsonDto) {
        if (characterJsonDto == null) {
            throw new ConditionNotMetException("로스트아크 서버에서 캐릭터를 찾을 수 없습니다.");
        }

        if (characterJsonDto.getItemMaxLevel() < 1415.00) {
            throw new ConditionNotMetException("로아투두는 아이템 레벨 1415 이상 캐릭터만 저장할 수 있습니다.");
        }
    }
}
