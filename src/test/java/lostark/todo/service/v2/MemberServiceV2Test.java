package lostark.todo.service.v2;

import lostark.todo.controller.v2.dto.characterDto.CharacterDayContentDto;
import lostark.todo.domain.character.Character;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceV2Test {
    private final String username = "qwe2487";
    @Autowired
    MemberServiceV2 memberServiceV2;

    @Test
    void updateCharacterList() {
        List<CharacterDayContentDto> characterDayContentDtoList = new ArrayList<>();
        CharacterDayContentDto 마볼링 = CharacterDayContentDto.builder()
                .characterName("마볼링")
                .chaosCheck(0)
                .chaosSelected(false)
                .guardianCheck(2)
                .guardianSelected(true)
                .build();

        CharacterDayContentDto 카카오볼링 = CharacterDayContentDto.builder()
                .characterName("카카오볼링")
                .chaosCheck(2)
                .chaosSelected(false)
                .guardianCheck(1)
                .guardianSelected(false)
                .build();

        characterDayContentDtoList.add(마볼링);
        characterDayContentDtoList.add(카카오볼링);
        List<CharacterDayContentDto> characters = memberServiceV2.updateCharacterList(username, characterDayContentDtoList);

        assertThat(characters.size()).isEqualTo(15);

        characters.stream()
                .filter(character -> character.getCharacterName().equals("마볼링"))
                .forEach(character -> {
                    assertThat(character.getGuardianCheck()).isEqualTo(2);
                    assertThat(character.isGuardianSelected()).isEqualTo(true);
                });

        characters.stream()
                .filter(character -> character.getCharacterName().equals("카카오볼링"))
                .forEach(character -> {
                    assertThat(character.getGuardianCheck()).isEqualTo(1);
                    assertThat(character.isGuardianSelected()).isEqualTo(false);
                });

    }
}