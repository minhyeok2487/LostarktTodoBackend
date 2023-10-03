package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todo.TodoRepository;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.domain.todoV2.TodoV2Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoV2Repository todoV2Repository;

    public Todo findById(long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 정보입니다."));
    }

    public Todo updateWeekCheck(TodoDto todoDto) {
        return findById(todoDto.getTodoId()).updateCheck();
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

    public Todo updateWeekMessage(TodoDto todoDto) {
        return findById(todoDto.getTodoId()).updateMessage(todoDto.getMessage());
    }

    public void updateWeek_V3(Character character, WeekContent weekContent) {
        TodoV2 todoV2 = TodoV2.builder()
                .weekContent(weekContent)
                .character(character)
                .isChecked(false)
                .gold(weekContent.getGold())
                .build();

        Optional<TodoV2> existingTodo = todoV2Repository.findByCharacterAndWeekContent(character, weekContent);

        if (existingTodo.isPresent()) {
            character.getTodoV2List().remove(existingTodo.get());
        } else {
            // 같은 카테고리, 같은 관문이 있다면 변경
            // 선택시 이전 관문이 선택 되어있어야함
            boolean categoryAndGate = true;
            boolean beforeGate = false;
            for (TodoV2 exited : character.getTodoV2List()) {
                if(exited.getWeekContent().getWeekCategory().equals(todoV2.getWeekContent().getWeekCategory())
                        && exited.getWeekContent().getGate() == todoV2.getWeekContent().getGate()) {
                    exited.updateWeekContent(weekContent);
                    categoryAndGate = false;
                    break;
                }
                if(todoV2.getWeekContent().getGate() == 1) {
                    beforeGate = true;
                }
                if(todoV2.getWeekContent().getGate() >=2
                        && exited.getWeekContent().getGate() == todoV2.getWeekContent().getGate()-1) {
                    beforeGate = true;
                }
            }

            if(categoryAndGate && beforeGate) {
                character.getTodoV2List().add(todoV2);
            }
            if(categoryAndGate && !beforeGate) {
                throw new IllegalArgumentException("이전 관문을 먼저 선택해주십시오.");
            }
        }
    }

    public void updateWeekAllV3(Character character, List<WeekContent> weekContentList) {
        WeekContent weekContent = weekContentList.get(0);
        List<TodoV2> updatedTodoV2List = new ArrayList<>();

        // 같은 카테고리, 같은 난이도 클릭?
        boolean checked = false;
        for (TodoV2 todoV2 : character.getTodoV2List()) {
            if (todoV2.getWeekContent().getWeekCategory().equals(weekContent.getWeekCategory()) &&
                    todoV2.getWeekContent().getWeekContentCategory().equals(weekContent.getWeekContentCategory())) {
                checked = true;
                break;
            }
        }

        character.getTodoV2List().removeIf(todoV2 ->
                todoV2.getWeekContent().getWeekCategory().equals(weekContent.getWeekCategory()));

        // 다른 난이도 클릭
        if(!checked) {
            for (WeekContent content : weekContentList) {
                TodoV2 todoV2 = TodoV2.builder()
                        .weekContent(content)
                        .character(character)
                        .isChecked(false)
                        .gold(content.getGold())
                        .build();
                updatedTodoV2List.add(todoV2);
            }
            character.getTodoV2List().addAll(updatedTodoV2List);
        }
    }


}