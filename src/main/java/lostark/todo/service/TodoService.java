package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterTodoDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todo.TodoContentName;
import lostark.todo.domain.todo.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public void updateWeek(CharacterTodoDto characterTodoDto, Character character, int gold) {
        Todo exist = todoRepository.findByCharacterAndContentName(character, characterTodoDto.getContentName());
        if(exist != null) {
            todoRepository.delete(exist);
        } else {
            // 같은 레이드 컨텐츠 일때 Exception 처리
            List<Todo> todoList = sameCategory(character, characterTodoDto.getContentName());
            Todo todo = Todo.builder()
                    .contentName(characterTodoDto.getContentName())
                    .isChecked(false)
                    .character(character)
                    .gold(gold)
                    .build();
            todoRepository.save(todo);
        }
    }

    public Todo updateWeekCheck(TodoDto todoDto) {
        return findById(todoDto.getTodoId()).updateCheck(todoDto.isTodoCheck());
    }

    // 같은 레이드 컨텐츠 일때 Exception 처리
    public List<Todo> sameCategory(Character character, TodoContentName contentName) {
        List<Todo> todoList = character.getTodoList();
        for (Todo todo : todoList) {
            if (todo.getContentName().getCategory().equals(contentName.getCategory())) {
                throw new IllegalArgumentException("같은 레이드 컨텐츠 입니다.");
            }
        }
        return todoList;
    }
}
