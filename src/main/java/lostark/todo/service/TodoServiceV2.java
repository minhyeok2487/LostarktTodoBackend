package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoSortRequestDto;
import lostark.todo.domainV2.character.dto.UpdateWeekRaidCheckRequest;
import lostark.todo.domainV2.character.dto.UpdateWeekRaidMessageRequest;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.keyvalue.KeyValueRepository;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.domain.todoV2.TodoV2Repository;
import lostark.todo.domainV2.util.content.dao.ContentDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoServiceV2 {

    private final TodoV2Repository todoV2Repository;
    private final KeyValueRepository keyValueRepository;
    private final ContentDao contentDao;

    public TodoV2 findById(long id) {
        return todoV2Repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 정보입니다."));
    }

    public List<TodoV2> findAll() {
        return todoV2Repository.findAll();
    }


    public TodoV2 updateWeekMessage(TodoDto todoDto) {
        return findById(todoDto.getTodoId()).updateMessage(todoDto.getMessage());
    }

    @Transactional
    public void updateWeekMessage(Character character, UpdateWeekRaidMessageRequest request) {
        character.getTodoV2List().stream()
                .filter(todoV2 -> todoV2.getId() == request.getTodoId())
                .findFirst()
                .ifPresent(todoV2 -> todoV2.updateMessage(request.getMessage()));
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
                .coolTime(checkTwoCycle(weekContent))
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

    // 2주기 레이드일때 등록된 값 체크해서 초기화주간 인지 확인
    // 초기화 주간이면 2, 아니면 1
    private int checkTwoCycle(WeekContent weekContent) {
        if (weekContent.getCoolTime() == 2) {
            return Integer.parseInt(keyValueRepository.findByKeyName("two-cycle"));
        } else {
            return 2;
        }
    }


    /**
     * 주간 레이드 추가/삭제(카테고리, 난이도 일괄)
     */
    public void updateWeekRaidAll(Character character, List<WeekContent> weekContentList) {
        WeekContent weekContent = weekContentList.get(0);
        String weekCategory = weekContent.getWeekCategory();

        // 삭제할 목록 찾기
        List<TodoV2> removedList = character.getTodoV2List().stream()
                .filter(todoV2 -> todoV2.getWeekContent().getWeekCategory().equals(weekCategory))
                .toList();

        // 삭제할 항목 제거
        if (!removedList.isEmpty()) {
            removedList.forEach(todoV2 -> {
                character.getTodoV2List().remove(todoV2);
                todoV2Repository.delete(todoV2);
            });
        } else {
            // 추가할 항목 생성 및 추가
            List<TodoV2> updatedTodoV2List = weekContentList.stream()
                    .map(content -> TodoV2.builder()
                            .weekContent(content)
                            .character(character)
                            .isChecked(false)
                            .gold(content.getGold())
                            .coolTime(checkTwoCycle(content))
                            .sortNumber(999)
                            .build())
                    .toList();

            character.getTodoV2List().addAll(updatedTodoV2List);
            todoV2Repository.saveAll(updatedTodoV2List);
        }
    }


    public void updateWeekRaidCheck(Character character, String weekCategory, int currentGate, int totalGate) {
        if (currentGate < totalGate) {
            TodoV2 result = todoV2Repository.findByCharacterAndWeekCategoryAndGate(character, weekCategory, currentGate + 1)
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

    @Transactional
    public void updateWeekRaidCheckAll(Character character, String weekCategory) {
        List<TodoV2> todoV2List = todoV2Repository.findAllCharacterAndWeekCategory(character, weekCategory);

        // 현재 체크된 항목이 전체 항목 수와 같은지 확인
        boolean allChecked = todoV2List.stream().allMatch(TodoV2::isChecked);

        // 전체가 체크되어 있으면 전체 체크 해제, 그렇지 않으면 전체 체크
        todoV2List.forEach(todoV2 -> todoV2.setChecked(!allChecked));
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

    @Transactional
    public void updateWeekRaidCheck(Character character, UpdateWeekRaidCheckRequest request) {
        List<WeekContent> weekContentList = contentDao.findAllByIdWeekContent(request.getWeekContentIdList())
                .stream()
                .map(content -> (WeekContent) content)
                .toList();

        String weekCategory = weekContentList.get(0).getWeekCategory();
        if (weekContentList.size() == 1) {
            if (request.getCurrentGate() < request.getTotalGate()) {
                TodoV2 result = todoV2Repository.findByCharacterAndWeekCategoryAndGate(
                        character, weekCategory, request.getCurrentGate() + 1)
                        .orElseThrow(() -> new IllegalArgumentException("이전 관문이 없습니다. 주간 숙제 관리에서 추가해주세요"));
                result.updateCheck();
            }
            if (request.getCurrentGate() == request.getTotalGate()) { //다시 0으로 돌아감
                List<TodoV2> todoV2List = todoV2Repository.findAllCharacterAndWeekCategory(character, weekCategory);
                for (TodoV2 todoV2 : todoV2List) {
                    todoV2.setChecked(false);
                }
            }
        } else {
            List<TodoV2> todoV2List = todoV2Repository.findAllCharacterAndWeekCategory(character, weekCategory);

            // 현재 체크된 항목이 전체 항목 수와 같은지 확인
            boolean allChecked = todoV2List.stream().allMatch(TodoV2::isChecked);

            // 전체가 체크되어 있으면 전체 체크 해제, 그렇지 않으면 전체 체크
            todoV2List.forEach(todoV2 -> todoV2.setChecked(!allChecked));
        }
    }
}