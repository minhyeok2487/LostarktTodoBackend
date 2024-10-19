package lostark.todo.domainV2.character.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.repository.CharacterRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CharacterDao {

    private final CharacterRepository characterRepository;

    @Transactional(readOnly = true)
    public List<Character> getCharacterList(String username) {
        return characterRepository.getCharacterList(username);
    }

    @Transactional(readOnly = true)
    public List<Character> getCharacter(String characterName) {
        return characterRepository.getCharacter(characterName);
    }
}
