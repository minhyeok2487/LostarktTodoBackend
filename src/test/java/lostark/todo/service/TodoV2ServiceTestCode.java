package lostark.todo.service;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.domain.todoV2.TodoV2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TodoV2ServiceTestCode {

    @Autowired
    TodoV2Repository todoV2Repository;

    public List<TodoV2> findTodoV2List(Character character) {
        return todoV2Repository.findAllByCharacter(character);
    }
}
