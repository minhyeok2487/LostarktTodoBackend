package lostark.todo.controller.apiV3.member;

import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
class MemberCharacterListControllerTest {

    @Autowired
    CharacterService characterService;

    @Autowired
    MemberService memberService;

    void findCharacterListCode(String username) {

        // username -> characterList 조회
        List<Character> characterList = characterService.findCharacterListUsername(username);

        // 결과
        Map<String, List<CharacterDto>> characterDtoMap = characterList.stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterDto().toDtoV2(character))
                .sorted(Comparator
                        .comparingInt(CharacterDto::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed()))
                .collect(Collectors.groupingBy(CharacterDto::getServerName));
    }

    @Test
    @DisplayName("캐릭터가 많이 등록된 사람 20개 테스트")
    void findCharacterListTest() {
        // given


        // then

    }
}