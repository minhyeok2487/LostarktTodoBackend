package lostark.todo.controller.api;

import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.todo.Todo;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CharacterApiControllerTest {

    @Autowired
    ContentService contentService;

    @Autowired
    CharacterService characterService;

    @Test
    void todoForm() {
        String characterName = "마볼링";
        String username = "repeat2487@gmail.com";
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);

        // 아이템 레벨보다 작은 컨텐츠 불러옴
        List<WeekContent> allByWeekContent = contentService.findAllByWeekContentWithItemLevel(character.getItemLevel());

        // 임시 id 70이하
        List<WeekContent> collect = allByWeekContent.stream()
                .filter(weekContent -> weekContent.getId() <= 70)
                .collect(Collectors.toList());

    }
}