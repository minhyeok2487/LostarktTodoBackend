package lostark.todo.service.v2;

import lostark.todo.controller.v2.dto.characterDto.CharacterUpdateDtoV2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class MemberServiceV2Test {
    private final String username = "qwe2487";
    @Autowired
    MemberServiceV2 memberServiceV2;

    @Test
    void updateCharacterList() {
        List<CharacterUpdateDtoV2> characterUpdateDtoV2List = new ArrayList<>();
        CharacterUpdateDtoV2 마볼링 = CharacterUpdateDtoV2.builder()
                .characterName("마볼링")
                //.chaosSelected(true)
                .chaosCheck(0)
                .guardianCheck(2)
                .guardianSelected(true)
                .build();

        CharacterUpdateDtoV2 카카오볼링 = CharacterUpdateDtoV2.builder()
                .characterName("카카오볼링")
                .chaosCheck(2)
                .chaosSelected(false)
                .guardianCheck(1)
                .guardianSelected(false)
                .build();


        characterUpdateDtoV2List.add(마볼링);
        characterUpdateDtoV2List.add(카카오볼링);
//        List<CharacterDayContentDto> characters = memberServiceV2.updateCharacterList(username, characterDayContentDtoList);
//
//        characters.stream()
//                .filter(character -> character.getCharacterName().equals("마볼링"))
//                .forEach(character -> {
//                    assertThat(character.isChaosSelected()).isEqualTo(false);
//                    assertThat(character.getChaosCheck()).isEqualTo(0);
//                    assertThat(character.getGuardianCheck()).isEqualTo(2);
//                    assertThat(character.isGuardianSelected()).isEqualTo(true);
//                });

//        characters.stream()
//                .filter(character -> character.getCharacterName().equals("카카오볼링"))
//                .forEach(character -> {
//                    assertThat(character.getGuardianCheck()).isEqualTo(1);
//                    assertThat(character.isGuardianSelected()).isEqualTo(false);
//                });

    }
}