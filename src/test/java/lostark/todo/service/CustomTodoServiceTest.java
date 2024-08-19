package lostark.todo.service;

import lostark.todo.controller.dtoV2.character.CreateCustomTodoRequest;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.customTodo.CustomTodo;
import lostark.todo.domain.customTodo.CustomTodoFrequencyEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomTodoServiceTest {

    @Autowired
    private CustomTodoService customTodoService;

    @Autowired CharacterService characterService;

    @Test
    @DisplayName("캐릭터에 커스텀 일일 숙제 등록 성공")
    void create_Success() {
        //given
        long characterId = 10L;
        String contentName = "일일 숙제";
        CustomTodoFrequencyEnum frequency = CustomTodoFrequencyEnum.DAILY;
        CreateCustomTodoRequest request = new CreateCustomTodoRequest(characterId, contentName, frequency);

        //when
        Character character = characterService.get(109568, "repeat2487@gmail.com");
        CustomTodo customTodo = customTodoService.create(character, request);

        //then
        Assertions.assertThat(customTodo).isNotNull();
        Assertions.assertThat(customTodo.getCharacter()).isEqualTo(character);
        Assertions.assertThat(customTodo.getFrequency()).isEqualTo(frequency);
        Assertions.assertThat(customTodo.getContentName()).isEqualTo(contentName);
        Assertions.assertThat(customTodo.isChecked()).isEqualTo(false);
    }
}