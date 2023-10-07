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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public TodoV2 findByIdV2(long id) {
        return todoV2Repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 정보입니다."));
    }

    public Todo updateWeekCheck(TodoDto todoDto) {
        return findById(todoDto.getTodoId()).updateCheck();
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }
    public List<TodoV2> findAllV2() {return todoV2Repository.findAll();}

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

    /**
     * 주간 숙제 추가/삭제(1개씩)
     */
    public void updateWeek_V3(Character character, WeekContent weekContent) {
        TodoV2 todoV2 = TodoV2.builder()
                .weekContent(weekContent)
                .character(character)
                .isChecked(false)
                .gold(weekContent.getGold())
                .coolTime(2)
                .build();

        //weekContent (아브렐슈드, 일리아칸 등)별로 이미 존재 하면 지움
        Optional<TodoV2> existingTodo = todoV2Repository.findByCharacterAndWeekContent(character, weekContent);

        if (existingTodo.isPresent()) {
            character.getTodoV2List().remove(existingTodo.get());
        } else {
            boolean categoryAndGate = true;
            boolean beforeGate = false;
            if (character.getTodoV2List().isEmpty()) {
                character.getTodoV2List().add(todoV2);
            } else {
                for (TodoV2 exited : character.getTodoV2List()) {
                    // 같은 weekContent, 같은 gate가 있다면 변경 (노말 <-> 하드)
                    if (exited.getWeekContent().getWeekCategory().equals(todoV2.getWeekContent().getWeekCategory())
                            && exited.getWeekContent().getGate() == todoV2.getWeekContent().getGate()) {
                        exited.updateWeekContent(weekContent);
                        categoryAndGate = false;
                        break;
                    }

                    // 선택시 이전 관문이 선택 되어있어야함
                    if (todoV2.getWeekContent().getGate() >= 2
                            && exited.getWeekContent().getGate() == todoV2.getWeekContent().getGate() - 1) {
                        beforeGate = true;
                    }
                    // 첫번째 관문은 이전 관문 상관없이 선택 가능
                    if (todoV2.getWeekContent().getGate() == 1) {
                        beforeGate = true;
                    }
                }

                // 같은 weekContent, 같은 gate가 있는게 아니고 이전 관문이 있다면 더함
                if (categoryAndGate && beforeGate) {
                    character.getTodoV2List().add(todoV2);
                }
                // 이전 관문이 없으면 Exception
                if (categoryAndGate && !beforeGate) {
                    throw new IllegalArgumentException("이전 관문을 먼저 선택해주십시오.");
                }
            }
        }
    }

    /**
     * 주간 숙제 추가/삭제(카테고리, 난이도 일괄)
     */
    public void updateWeekAllV3(Character character, List<WeekContent> weekContentList) {
        WeekContent weekContent = weekContentList.get(0);
        List<TodoV2> updatedTodoV2List = new ArrayList<>();
        boolean check = false;
        // 하나라도 선택 되어 있으면 삭제
        for (TodoV2 todoV2 : character.getTodoV2List()) {
            if (todoV2.getWeekContent().getWeekCategory().equals(weekContent.getWeekCategory())) {
                check = true;
            }
        }

        character.getTodoV2List().removeIf(todoV2 ->
                todoV2.getWeekContent().getWeekCategory().equals(weekContent.getWeekCategory()));

        // 하나도 없으면 전체 추가
        if(!check) {
            for (WeekContent content : weekContentList) {
                TodoV2 todoV2 = TodoV2.builder()
                        .weekContent(content)
                        .character(character)
                        .isChecked(false)
                        .gold(content.getGold())
                        .coolTime(2)
                        .build();
                updatedTodoV2List.add(todoV2);
            }
            character.getTodoV2List().addAll(updatedTodoV2List);
        }
    }


    public void updateWeekCheckV3(Character character, String weekCategory, int currentGate, int totalGate) {
        if (currentGate<totalGate) {
            TodoV2 result = todoV2Repository.findByCharacterAndWeekCategoryAndGate(character, weekCategory, currentGate+1)
                    .orElseThrow(() -> new IllegalArgumentException(""));
            result.updateCheck();
        }
        if (currentGate == totalGate) {
            List<TodoV2> todoV2List = todoV2Repository.findAllCharacterAndWeekCategory(character, weekCategory);
            for (TodoV2 todoV2 : todoV2List) {
                todoV2.setChecked(false);
            }
        }

    }


}