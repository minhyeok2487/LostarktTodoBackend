package lostark.todo.service;

import lostark.todo.controller.dto.characterDto.CharacterRequestDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CharacterServiceTest {

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    EntityManager em;

    @Test
    void 동일한캐릭터_이름의_데이터수정() {
        CharacterRequestDto characterRequestDto = new CharacterRequestDto();
        characterRequestDto.setCharacterName("마볼링");
        characterRequestDto.setGuardian(2);
        characterRequestDto.setGuardianGauge(100);
        characterRequestDto.setChaos(1);
        characterRequestDto.setChaosGauge(100);
        Character character = characterRepository.findByCharacterName(characterRequestDto.getCharacterName());
        character.getCharacterContent().update(characterRequestDto);

        em.flush();
        em.clear();

        Character updatedCharacter = characterRepository.findByCharacterName(characterRequestDto.getCharacterName());
        assertThat(updatedCharacter.getCharacterContent().getChaosGauge()).isEqualTo(1000);

    }
}