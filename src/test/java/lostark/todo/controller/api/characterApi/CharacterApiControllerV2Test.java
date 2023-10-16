package lostark.todo.controller.api.characterApi;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.service.CharacterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class CharacterApiControllerV2Test {

    @Autowired
    CharacterService characterService;

    @Test
    @DisplayName("골드 획득 캐릭터 지정 테스트")
    void updateGoldCharacter() {
    }

    @Test
    void updateChallenge() {
    }

    @Test
    void updateSettings() {
    }
}