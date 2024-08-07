package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoSortRequestDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.domain.todoV2.TodoV2Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoServiceV2 {

    private final TodoV2Repository todoV2Repository;

    public TodoV2 findById(long id) {
        return todoV2Repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 정보입니다."));
    }

    public List<TodoV2> findAll() {return todoV2Repository.findAll();}


    public TodoV2 updateWeekMessage(TodoDto todoDto) {
        return findById(todoDto.getTodoId()).updateMessage(todoDto.getMessage());
    }

    //주간 레이드 추가/삭제(1개씩)
    @Transactional
    public void updateWeekRaid(Character character, WeekContent weekContent) {
        TodoV2 existingTodo = findExistingTodo(character, weekContent);

        if (existingTodo == null) {
            createNewTodo(character, weekContent);
        } else {
            deleteTodo(character, existingTodo, weekContent);
        }
    }

    private TodoV2 findExistingTodo(Character character, WeekContent weekContent) {
        return character.getTodoV2List().stream()
                .filter(todo -> todo.getWeekContent().getId() == weekContent.getId())
                .findFirst()
                .orElse(null);
    }

    private void createNewTodo(Character character, WeekContent weekContent) {
        TodoV2 newTodo = TodoV2.builder()
                .weekContent(weekContent)
                .character(character)
                .isChecked(false)
                .gold(weekContent.getGold())
                .coolTime(2)
                .sortNumber(999)
                .build();

        if (character.getTodoV2List().isEmpty()) {
            if (weekContent.getGate() == 1) {
                character.getTodoV2List().add(newTodo);
                todoV2Repository.save(newTodo);
            } else {
                throw new IllegalArgumentException("이전 관문을 먼저 선택해주십시오.");
            }
        } else {
            handleExistingTodo(character, newTodo);
        }
    }

    private void handleExistingTodo(Character character, TodoV2 newTodo) {
        boolean hasCategoryAndGate = false;
        boolean hasPreviousGate = false;

        for (TodoV2 existingTodo : character.getTodoV2List()) {
            if (isSameCategoryAndGate(existingTodo, newTodo)) {
                // 같은 weekContent, 같은 gate가 있다면 변경 (노말 <-> 하드)
                existingTodo.updateWeekContent(newTodo.getWeekContent());
                hasCategoryAndGate = true;
                break;
            }

            if (isPreviousGate(existingTodo, newTodo)) {
                //이전 관문 있는지 확인
                hasPreviousGate = true;
            }
        }

        if (!hasCategoryAndGate && hasPreviousGate) {
            character.getTodoV2List().add(newTodo);
            todoV2Repository.save(newTodo);
        } else if (!hasPreviousGate) {
            throw new IllegalArgumentException("이전 관문을 먼저 선택해주십시오.");
        }
    }

    private boolean isSameCategoryAndGate(TodoV2 existingTodo, TodoV2 newTodo) {
        return existingTodo.getWeekContent().getWeekCategory().equals(newTodo.getWeekContent().getWeekCategory())
                && existingTodo.getWeekContent().getGate() == newTodo.getWeekContent().getGate();
    }

    private boolean isPreviousGate(TodoV2 existingTodo, TodoV2 newTodo) {
        return (existingTodo.getWeekContent().getWeekCategory().equals(newTodo.getWeekContent().getWeekCategory())
                && newTodo.getWeekContent().getGate() >= 2
                && existingTodo.getWeekContent().getGate() == newTodo.getWeekContent().getGate() - 1)
                || newTodo.getWeekContent().getGate() == 1;
    }

    private void deleteTodo(Character character, TodoV2 existingTodo, WeekContent weekContent) {
        // 상위 관문이 존재하는지 확인
        boolean hasHigherGate = character.getTodoV2List().stream()
                .anyMatch(todo -> todo.getWeekContent().getWeekCategory().equals(weekContent.getWeekCategory()) &&
                        todo.getWeekContent().getGate() > weekContent.getGate());

        if (hasHigherGate) {
            throw new IllegalStateException("상위 관문을 먼저 제거하여 주십이오.");
        }

        // 상위 관문이 존재하지 않으면 해당 TodoV2 삭제
        character.getTodoV2List().remove(existingTodo);
        todoV2Repository.delete(existingTodo);
    }




    /**
     * 주간 레이드 추가/삭제(카테고리, 난이도 일괄)
     */
    public void updateWeekRaidAll(Character character, List<WeekContent> weekContentList) {
        WeekContent weekContent = weekContentList.get(0);
        List<TodoV2> updatedTodoV2List = new ArrayList<>();
        List<TodoV2> removedList = new ArrayList<>();
        boolean check = false;
        // 하나라도 선택 되어 있으면 삭제
        for (TodoV2 todoV2 : character.getTodoV2List()) {
            if (todoV2.getWeekContent().getWeekCategory().equals(weekContent.getWeekCategory())) {
                removedList.add(todoV2);
                check = true;
            }
        }

        if(!removedList.isEmpty()) {
            for (TodoV2 todoV2 : removedList) {
                character.getTodoV2List().remove(todoV2);
                todoV2Repository.delete(todoV2);
            }
        }

        // 하나도 없으면 전체 추가
        if(!check) {
            for (WeekContent content : weekContentList) {
                TodoV2 todoV2 = TodoV2.builder()
                        .weekContent(content)
                        .character(character)
                        .isChecked(false)
                        .gold(content.getGold())
                        .coolTime(2)
                        .sortNumber(999)
                        .build();
                updatedTodoV2List.add(todoV2);
                todoV2Repository.save(todoV2);
            }
            character.getTodoV2List().addAll(updatedTodoV2List);
        }
    }


    public void updateWeekRaidCheck(Character character, String weekCategory, int currentGate, int totalGate) {
        if (currentGate<totalGate) {
            TodoV2 result = todoV2Repository.findByCharacterAndWeekCategoryAndGate(character, weekCategory, currentGate+1)
                    .orElseThrow(() -> new IllegalArgumentException("이전 관문이 없습니다. 주간 숙제 관리에서 추가해주세요"));
            result.updateCheck();
        }
        if (currentGate == totalGate) {
            List<TodoV2> todoV2List = todoV2Repository.findAllCharacterAndWeekCategory(character, weekCategory);
            for (TodoV2 todoV2 : todoV2List) {
                todoV2.setChecked(false);
            }
        }

    }


    public void updateWeekRaidCheckAll(Character character, String weekCategory) {
        List<TodoV2> todoV2List = todoV2Repository.findAllCharacterAndWeekCategory(character, weekCategory);
        // 전체 체크 상태가 아니라면 전체 체크, 아니면 전체 체크 해제
        List<TodoV2> checked = todoV2List.stream().filter(TodoV2::isChecked).toList();
        for (TodoV2 todoV2 : todoV2List) {
            if (checked.size() == todoV2List.size()) {
                if (todoV2.getCoolTime() != 0) { //2주기 레이드 체크 방지
                    todoV2.setChecked(false);
                }
            } else {
                todoV2.setChecked(true);
            }

        }
    }

    // 캐릭터 보스별로 순서 정렬
    public void updateWeekRaidSort(Character character, List<TodoSortRequestDto> dtos) {
        for (TodoSortRequestDto dto : dtos) {
            List<TodoV2> todoV2List = todoV2Repository.findByCharacterAndWeekCategory(character, dto.getWeekCategory());

            if (!todoV2List.isEmpty()) {
                for (TodoV2 todoV2 : todoV2List) {
                    todoV2.setSortNumber(dto.getSortNumber());
                }
            }
        }
    }
}