package lostark.todo.service.v2.memberServiceV2;

import lostark.todo.controller.dto.characterDto.CharacterUpdateDto;
import lostark.todo.controller.dto.characterDto.CharacterUpdateListDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.v2.CharacterServiceV2;
import lostark.todo.service.v2.MemberServiceV2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class updateCharacterListTest {
    private String username = "qwe2487";
    private Member member = new Member();
    private CharacterUpdateDto 마볼링 = new CharacterUpdateDto();
    private CharacterUpdateDto 카카오볼링 = new CharacterUpdateDto();
    private CharacterUpdateDto 서예볼링 = new CharacterUpdateDto();

    @Autowired
    MemberServiceV2 memberService;

    @Autowired
    CharacterServiceV2 characterService;

    @Autowired
    Validator validator;

    @BeforeEach
    @DisplayName("테스트 시작전 캐릭터 데이터 생성")
    void init() {
        member = memberService.findMember(username);
        for (Character character : member.getCharacters()) {
            CharacterUpdateDto data = CharacterUpdateDto.builder()
                    .characterName(character.getCharacterName())
                    .chaosSelected(character.getCharacterDayContent().isChaosSelected())
                    .chaosCheck(character.getCharacterDayContent().getChaosCheck())
                    .guardianCheck(character.getCharacterDayContent().getGuardianCheck())
                    .guardianSelected(character.getCharacterDayContent().isGuardianSelected())
                    .build();
            if (character.getCharacterName().equals("마볼링")) {
                마볼링 = data;
            }
            if (character.getCharacterName().equals("카카오볼링")) {
                카카오볼링 = data;
            }
            if (character.getCharacterName().equals("서예볼링")) {
                서예볼링 = data;
            }
        }
    }

    @Test
    @DisplayName("캐릭터 리스트 업데이트 정상 테스트")
    void updateCharacterList() {
        /**
         * Given
         */
        CharacterUpdateListDto characterUpdateDtoV2List = new CharacterUpdateListDto();

        // 변경할 데이터
        CharacterUpdateDto update = 마볼링;
        update.setChaosSelected(!마볼링.getChaosSelected());
        update.setGuardianCheck(2);


        /**
         * When
         */
        characterUpdateDtoV2List.addCharacter(update);
        characterUpdateDtoV2List.addCharacter(카카오볼링);
        characterUpdateDtoV2List.addCharacter(서예볼링);
        CharacterUpdateListDto updateList = memberService.updateCharacterList(username, characterUpdateDtoV2List);

        /**
         * Then
         */
        assertThat(updateList.getCharacterUpdateDtoList().size()).isEqualTo(3);

        Character character = characterService.findCharacter("마볼링");
        CharacterUpdateDto after = CharacterUpdateDto.builder()
                .characterName(character.getCharacterName())
                .chaosSelected(character.getCharacterDayContent().isChaosSelected())
                .chaosCheck(character.getCharacterDayContent().getChaosCheck())
                .guardianCheck(character.getCharacterDayContent().getGuardianCheck())
                .guardianSelected(character.getCharacterDayContent().isGuardianSelected())
                .build();

        assertThat(update).isEqualTo(after);

    }

    @Test
    @DisplayName("캐릭터 리스트 업데이트 Exception 테스트")
    void updateCharacterListException() {
        /**
         * Given
         * null, 범위 초과등의 Exception용 데이터
         */
        CharacterUpdateDto testData1 = CharacterUpdateDto.builder()
                .characterName(마볼링.getCharacterName())
                .chaosCheck(3)
                .guardianCheck(-1)
                .guardianSelected(true)
                .build();
        System.out.println("testData1 = " + testData1);

        /**
         * When
         * Valid 검증
         */
        Set<ConstraintViolation<CharacterUpdateDto>> validation = validator.validate(testData1);

        /**
         * Then
         */
        Iterator<ConstraintViolation<CharacterUpdateDto>> iterator = validation.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<CharacterUpdateDto> next = iterator.next();
            messages.add(next.getMessage());
            System.out.println("message = " + next.getMessage());
        }

        Assertions.assertThat(messages).contains(
                "chaosCheck: 0~2 사이여야 합니다.",
                "chaosSelected: null일 수 없습니다.",
                "guardianCheck: 0~1 사이여야 합니다.");
    }
}