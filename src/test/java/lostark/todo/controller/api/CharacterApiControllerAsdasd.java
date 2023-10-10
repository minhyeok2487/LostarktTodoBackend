package lostark.todo.controller.api;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
class CharacterApiControllerAsdasd {

    @Autowired
    ContentService contentService;

    @Autowired
    CharacterService characterService;

    @Autowired
    TodoService todoService;

    @Test
    void todoForm() {
        String characterName = "마볼링";
        String username = "repeat2487@gmail.com";
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);

        // 아이템 레벨보다 작은 컨텐츠 불러옴
        List<WeekContent> allByWeekContent = contentService.findAllByWeekContentWithItemLevelV2(character.getItemLevel());

        // 임시 id 70이하
        List<WeekContent> collect = allByWeekContent.stream()
                .filter(weekContent -> weekContent.getId() <= 70)
                .collect(Collectors.toList());

    }
}