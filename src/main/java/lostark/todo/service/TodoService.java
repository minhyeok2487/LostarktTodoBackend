package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterTodoDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todo.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public Todo findById(long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 정보입니다."));
    }

    public Todo addWeek(CharacterTodoDto characterTodoDto, Character character, int gold) {
        Todo todo = Todo.builder()
                .contentName(characterTodoDto.getContentName())
                .isChecked(false)
                .character(character)
                .gold(gold)
                .build();
        return todoRepository.save(todo);
    }

    public Todo updateWeekCheck(TodoDto todoDto) {
        return findById(todoDto.getTodoId()).updateCheck(todoDto.isTodoCheck());
    }
}
