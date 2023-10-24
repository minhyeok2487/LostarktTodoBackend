package lostark.todo.controller.apiV2.characterApi;

import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domain.character.Character;
import lostark.todo.service.CharacterService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
class WeekContentApiControllerV2Test {

    @Autowired
    CharacterService characterService;
    @Test()
    @DisplayName("캐릭터 주간 에포나 체크 업데이트 테스트")
    void updateWeekTodoEponaCheckTest() {
        //given
        String username = "repeat2487@gmail.com";
        CharacterDefaultDto characterDefaultDto = CharacterDefaultDto.builder()
                .characterId(161)
                .characterName("마볼링")
                .build();
        Character character = characterService.findCharacter(
                characterDefaultDto.getCharacterId(), characterDefaultDto.getCharacterName(), username);
        int defaultValue = character.getWeekTodo().getWeekEpona();

        //when
        characterService.updateWeekEpona(character);
        CharacterDto responseDto = new CharacterDto().toDtoV2(character);

        //then
        if (defaultValue<3) {
            Assertions.assertThat(responseDto.getWeekEpona()).isEqualTo(defaultValue+1);
        } else {
            Assertions.assertThat(responseDto.getWeekEpona()).isEqualTo(0);
        }

    }
}