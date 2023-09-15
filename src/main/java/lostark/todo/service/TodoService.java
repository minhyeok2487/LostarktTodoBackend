package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterTodoDto;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
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
        Todo exist = todoRepository.findByCharacterAndContentName(character, characterTodoDto.getContentName().getDisplayName());
        if (exist != null) {
            todoRepository.delete(exist);
        } else {
            // 같은 레이드 컨텐츠 일때 컨텐츠 바꾸기
            List<Todo> todoList = character.getTodoList();
            for (Todo todo : todoList) {
                if (todo.getContentName().equals(characterTodoDto.getContentName().getCategory())) {
                    todo.updateContentName(characterTodoDto.getContentName().getDisplayName(), gold);
                    return;
                }
            }

            if (character.getTodoList().size() >= 3) {
                throw new IllegalArgumentException("주간 숙제 관리는 최대 3개까지만 가능합니다");
            } else {
                Todo todo = Todo.builder()
                        .contentName(characterTodoDto.getContentName().getDisplayName())
                        .isChecked(false)
                        .character(character)
                        .gold(gold)
                        .build();
                todoRepository.save(todo);
            }
        }
    }

    public Todo updateWeekCheck(TodoDto todoDto) {
        return findById(todoDto.getTodoId()).updateCheck(todoDto.isTodoCheck());
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public List<Todo> updateWeek_V2(Character character, WeekContentDto weekContentDto) {
        List<Todo> todoList = character.getTodoList();
        for (Todo todo : todoList) {
            if (todo.getName().equals(weekContentDto.getName())) {
                todoRepository.delete(todo);
                todoList.remove(todo);
                return todoList;
            }
            if (todo.getWeekCategory().equals(weekContentDto.getWeekCategory())) {
                Todo updated = todo.updateContent(weekContentDto);
                todoList.remove(todo);
                todoList.add(updated);
                return todoList;
            }
        }
        Todo build = Todo.builder()
                .contentName(weekContentDto.getName())
                .name(weekContentDto.getName())
                .weekCategory(weekContentDto.getWeekCategory())
                .isChecked(false)
                .character(character)
                .gold(weekContentDto.getGold())
                .build();
        todoRepository.save(build);
        todoList.add(build);
        return todoList;
    }

}