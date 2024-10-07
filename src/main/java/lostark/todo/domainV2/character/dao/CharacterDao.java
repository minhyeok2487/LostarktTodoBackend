package lostark.todo.domainV2.character.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.repository.CharacterRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CharacterDao {

    private final CharacterRepository characterRepository;

    public List<Character> getCharacterList(String username) {
        return characterRepository.getCharacterList(username);
    }
}
