package lostark.todo.data;

import lostark.todo.domain.util.content.enums.Category;
import lostark.todo.domain.util.content.entity.DayContent;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.DayTodo;
import lostark.todo.domain.character.entity.Settings;
import lostark.todo.domain.character.entity.WeekTodo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CharacterTestData {

    public static List<Character> createMockCharacterList() {
        Map<Category, List<DayContent>> dayContent = ContentTestData.createMockDayContentMap();

        return Arrays.asList(
                createCharacter("마볼링", 60, "바드", "실리안", 1620.0, 0, dayContent),
                createCharacter("늑대처럼울어", 60, "워로드", "실리안", 1610.0, 1, dayContent),
                createCharacter("브리또", 60, "건슬링어", "실리안", 1605.0, 2, dayContent),
                createCharacter("마법소녀", 60, "소서리스", "실리안", 1580.0, 3, dayContent),
                createCharacter("싸움꾼", 60, "스트라이커", "실리안", 1540.0, 4, dayContent),
                createCharacter("마법사", 60, "아르카나", "실리안", 1520.0, 5, dayContent)
        );
    }

    private static Character createCharacter(
            String characterName,
            int characterLevel,
            String characterClassName,
            String serverName,
            double itemLevel,
            int sortNumber,
            Map<Category, List<DayContent>> dayContent) {

        Character character = Character.builder()
                .characterName(characterName)
                .characterLevel(characterLevel)
                .characterClassName(characterClassName)
                .serverName(serverName)
                .itemLevel(itemLevel)
                .dayTodo(new DayTodo())
                .weekTodo(new WeekTodo())
                .build();
        character.setSettings(new Settings());
        character.setTodoV2List(new ArrayList<>());
        character.setSortNumber(sortNumber);
        // 캐릭터 이미지는 테스트용 URL
        character.setCharacterImage("https://test-image.com/" + characterName);

        character.getDayTodo().createDayContent(
                dayContent.get(Category.카오스던전),
                dayContent.get(Category.가디언토벌),
                character.getItemLevel());

        return character;
    }
}
