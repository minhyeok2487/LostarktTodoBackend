package lostark.todo.domain.character.repository;

import lostark.todo.domain.character.entity.Character;

public interface TodoV2CustomRepository {

    void resetTodoV2();

    void removeCharacter(Character character);

    void deleteByIdSafe(Long id);
}
