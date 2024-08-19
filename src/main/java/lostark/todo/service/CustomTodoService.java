package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.character.CheckCustomTodoRequest;
import lostark.todo.controller.dtoV2.character.CreateCustomTodoRequest;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.customTodo.CustomTodo;
import lostark.todo.domain.customTodo.CustomTodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lostark.todo.constants.ErrorMessages.CUSTOM_TODO_NOT_FOUND;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomTodoService {

    private final CustomTodoRepository customTodoRepository;

    @Transactional
    public CustomTodo get(Long customTodoId) {
        return customTodoRepository.findById(customTodoId)
                .orElseThrow(() -> new IllegalArgumentException(CUSTOM_TODO_NOT_FOUND));
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
    public void check(Character character, CheckCustomTodoRequest request) {
        CustomTodo customTodo = get(request.getCustomTodoId());
        if (customTodo.getCharacter().getId() == character.getId()) {
            customTodo.check();
        } else {
            throw new IllegalArgumentException(CUSTOM_TODO_NOT_FOUND);
        }
    }

    @Transactional
    public void remove(List<Long> characterIdList, Long customTodoId) {
        CustomTodo customTodo = get(customTodoId);
        if (characterIdList.contains(customTodo.getCharacter().getId())) {
            customTodoRepository.deleteById(customTodoId);
        } else {
            throw new IllegalArgumentException(CUSTOM_TODO_NOT_FOUND);
        }
    }
}