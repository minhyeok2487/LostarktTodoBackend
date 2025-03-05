package lostark.todo.domain.character.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.character.CheckCustomTodoRequest;
import lostark.todo.controller.dtoV2.character.CreateCustomTodoRequest;
import lostark.todo.controller.dtoV2.character.UpdateCustomTodoRequest;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.entity.CustomTodo;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.character.repository.CustomTodoRepository;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lostark.todo.global.exhandler.ErrorMessageConstants.CUSTOM_TODO_NOT_FOUND;
import static lostark.todo.global.exhandler.ErrorMessageConstants.FRIEND_PERMISSION_DENIED;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomTodoService {

    private final CustomTodoRepository customTodoRepository;

    @Transactional
    public CustomTodo get(Long customTodoId) {
        return customTodoRepository.get(customTodoId, null);
    }

    @Transactional
    public CustomTodo create(Character character, CreateCustomTodoRequest request) {
        CustomTodo customTodo = CustomTodo.builder()
                .contentName(request.getContentName())
                .isChecked(false)
                .frequency(request.getFrequency())
                .character(character)
                .build();
        return customTodoRepository.save(customTodo);
    }

    @Transactional(readOnly = true)
    public List<CustomTodo> search(String username) {
        return customTodoRepository.search(username);
    }

    @Transactional
    public void update(Character character, UpdateCustomTodoRequest request, Long customTodoId) {
        customTodoRepository.get(customTodoId, character.getId()).update(request.getContentName());
    }

    @Transactional
    public void check(Character character, CheckCustomTodoRequest request) {
        customTodoRepository.get(request.getCustomTodoId(), character.getId()).check();
    }

    @Transactional
    public void remove(List<Long> characterIdList, Long customTodoId) {
        CustomTodo customTodo = get(customTodoId);
        if (characterIdList.contains(customTodo.getCharacter().getId())) {
            customTodoRepository.deleteById(customTodoId);
        } else {
            throw new ConditionNotMetException(CUSTOM_TODO_NOT_FOUND);
        }
    }

    @Transactional
    public void friendCheck(Character character, CheckCustomTodoRequest request, Friends friend) {
        CustomTodo customTodo = get(request.getCustomTodoId());

        if (customTodo.getCharacter().getId() != character.getId()) {
            throw new ConditionNotMetException(CUSTOM_TODO_NOT_FOUND);
        }

        boolean canCheckTodo = customTodo.getFrequency().equals(CustomTodoFrequencyEnum.DAILY)
                ? friend.getFriendSettings().isCheckDayTodo()
                : friend.getFriendSettings().isCheckWeekTodo();

        if (canCheckTodo) {
            customTodo.check();
        } else {
            throw new ConditionNotMetException(FRIEND_PERMISSION_DENIED);
        }
    }

    @Transactional
    public void deleteMyMember(Member member) {
        customTodoRepository.deleteByMember(member);
    }
}