package lostark.todo.domain.character.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.*;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.content.entity.WeekContent;
import lostark.todo.domain.logs.service.LogService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import lostark.todo.global.keyvalue.KeyValueRepository;
import lostark.todo.domain.character.entity.TodoV2;
import lostark.todo.domain.character.repository.TodoV2Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoServiceV2 {

    private final TodoV2Repository todoV2Repository;
    private final KeyValueRepository keyValueRepository;
    private final LogService logService;

    // 캐릭터 주간 레이드 Message 수정
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
        Optional.ofNullable(findExistingTodo(character, weekContent)) // 이미 해당 레이드가 존재하는지 확인
                .ifPresentOrElse(
                        existingTodo -> deleteTodo(character, existingTodo, weekContent), // 존재하면 삭제
                        () -> createNewTodo(character, weekContent) // 없으면 추가
                );
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
                .characterGold(weekContent.getCharacterGold())
                .coolTime(checkTwoCycle(weekContent))
                .sortNumber(999)
                .build();

        if (character.getTodoV2List().isEmpty()) {
            handleNewTodo(character, weekContent, newTodo);
        } else {
            handleExistingTodo(character, newTodo);
        }
    }

    private void handleNewTodo(Character character, WeekContent weekContent, TodoV2 newTodo) {
        if (weekContent.getGate() == 1) {
            character.getTodoV2List().add(newTodo);
            todoV2Repository.save(newTodo);
        } else {
            throw new ConditionNotMetException("이전 관문을 먼저 선택해주십시오.");
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
            throw new ConditionNotMetException("이전 관문을 먼저 선택해주십시오.");
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
            throw new ConditionNotMetException("상위 관문을 먼저 제거하여 주십이오.");
        }

        // 상위 관문이 존재하지 않으면 해당 TodoV2 삭제
        character.getTodoV2List().remove(existingTodo);
        todoV2Repository.deleteByIdSafe(existingTodo.getId());
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
                todoV2Repository.deleteByIdSafe(todoV2.getId());
            });
        } else {
            // 추가할 항목 생성 및 추가
            List<TodoV2> updatedTodoV2List = weekContentList.stream()
                    .map(content -> TodoV2.builder()
                            .weekContent(content)
                            .character(character)
                            .isChecked(false)
                            .gold(content.getGold())
                            .characterGold(content.getCharacterGold())
                            .coolTime(checkTwoCycle(content))
                            .sortNumber(999)
                            .build())
                    .toList();

            character.getTodoV2List().addAll(updatedTodoV2List);
            todoV2Repository.saveAll(updatedTodoV2List);
        }
    }

    @Transactional
    public CharacterResponse updateWeekRaidCheck(Character character, UpdateWeekRaidCheckRequest request) {
        List<TodoV2> todoV2List = todoV2Repository.findAllCharacterAndWeekCategory(character, request.getWeekCategory());
        if (todoV2List.isEmpty()) {
            throw new ConditionNotMetException("등록된 숙제가 아닙니다.");
        }
        boolean allChecked = todoV2List.stream().allMatch(TodoV2::isChecked);

        // 전체 체크 API
        if (request.isAllCheck()) {
            // 숙제 전체가 체크되어 있다면 전체 체크해제
            // 하나라도 체크가 안되어있으면 전체 체크
            todoV2List.forEach(todoV2 -> {
                todoV2.setChecked(!allChecked);
                todoV2.setMoreRewardCheck(false);
            });
        } else {
            // 단건 체크 API
            if (allChecked) {
                // 전체 체크 되어 있으면 체크 해제
                todoV2List.forEach(todoV2 -> {
                    todoV2.setChecked(false);
                    todoV2.setMoreRewardCheck(false);
                });
            } else {
                // 마지막 체크된 항목 찾기
                List<TodoV2> findList = todoV2List.stream()
                        .filter(TodoV2::isChecked)
                        .toList();

                if (!findList.isEmpty()) {
                    int currentGate = findList.get(findList.size() - 1).getWeekContent().getGate();
                    // 다음 관문 체크
                    todoV2List.stream()
                            .filter(todoV2 -> todoV2.getWeekContent().getGate() == currentGate + 1)
                            .findFirst()
                            .ifPresent(next -> next.setChecked(true));
                } else {
                    // 체크된 항목이 없으면 gate == 1인 항목 체크
                    todoV2List.stream()
                            .filter(todoV2 -> todoV2.getWeekContent().getGate() == 1)
                            .findFirst()
                            .ifPresent(firstGate -> firstGate.setChecked(true));
                }
            }
        }

        CharacterResponse response = new CharacterResponse().toDto(character);

        logService.processWeekLog(request, response);

        return response;
    }


    @Transactional
    // 캐릭터 보스별로 순서 정렬
    public void updateWeekRaidSort(Character character, List<UpdateWeekRaidSortRequest.SortRequest> request) {
        request.stream()
                .map(sortRequest -> todoV2Repository.findByCharacterAndWeekCategory(character, sortRequest.getWeekCategory()))
                .filter(todoV2List -> !todoV2List.isEmpty())
                .forEach(todoV2List -> todoV2List.forEach(todoV2 -> todoV2.setSortNumber(
                        request.stream()
                                .filter(sr -> sr.getWeekCategory().equals(todoV2.getWeekContent().getWeekCategory()))
                                .findFirst()
                                .map(UpdateWeekRaidSortRequest.SortRequest::getSortNumber)
                                .orElse(todoV2.getSortNumber())
                )));
    }

    @Transactional
    public CharacterResponse updateRaidMoreRewardCheck(Character updateCharacter, UpdateWeekRaidMoreRewardCheckRequest request) {
        updateCharacter.getTodoV2List().stream()
                .filter(todoV2 -> todoV2.getWeekContent().getWeekCategory().equals(request.getWeekCategory())
                        && todoV2.getWeekContent().getGate() == request.getGate())
                .findFirst()
                .ifPresent(TodoV2::updateRaidMoreRewardCheck);
        CharacterResponse response = new CharacterResponse().toDto(updateCharacter);
        logService.processWeekMoreRewardLog(request, response);
        return response;
    }

    @Transactional
    public CharacterResponse updateElysian(Character updateCharacter, UpdateElysianRequest request) {
        switch (request.getAction()) {
            case INCREMENT -> updateCharacter.getWeekTodo().incrementElysianCount();
            case DECREMENT -> updateCharacter.getWeekTodo().decrementElysianCount();
        }
        return new CharacterResponse().toDto(updateCharacter);
    }

    @Transactional
    public CharacterResponse updateElysianAll(Character updateCharacter) {
        updateCharacter.getWeekTodo().resetElysianCount();
        return new CharacterResponse().toDto(updateCharacter);
    }
}