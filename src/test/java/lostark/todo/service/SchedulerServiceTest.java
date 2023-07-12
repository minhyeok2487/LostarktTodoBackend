package lostark.todo.service;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class SchedulerServiceTest {

    @Autowired CharacterRepository characterRepository;

}