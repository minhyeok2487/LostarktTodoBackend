package lostark.todo.service;

import lostark.todo.controller.dto.characterDto.CharacterTodoDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todo.TodoContentName;
import lostark.todo.domain.todo.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private Todo todo;

    @InjectMocks
    private TodoService todoService;

    @Test
    @DisplayName("updateWeek 성공(해당 컨텐츠가 캐릭터에 저장되어 있지 않음)")
    void updateWeekExistNull() {
        // Given
        String characterName = "마볼링";
        TodoContentName todoContentName = TodoContentName.상아탑_하드;
        CharacterTodoDto characterTodoDto = CharacterTodoDto.builder()
                .characterName(characterName)
                .contentName(todoContentName)
                .build();

        Character character = new Character();
        character.setCharacterName(characterName);
        character.setTodoList(new ArrayList<>());

        int gold = 1000;

        // 해당 컨텐츠가 캐릭터에 저장되어 있지 않음
        when(todoRepository.findByCharacterAndContentName(eq(character), eq(todoContentName)))
                .thenReturn(null);

        // When
        Throwable throwable = catchThrowable(() -> todoService.updateWeek(characterTodoDto, character, gold));

        // Then
        assertThat(throwable).isNull(); //예외 발생X
        verify(todoRepository, never()).delete(any()); //delete 메서드 호출X
        verify(todoRepository, times(1)).save(any()); //save 메서드 한번 호출
    }

    @Test
    @DisplayName("updateWeek 성공(이미 존재하는 컨텐츠 삭제)")
    void updateWeekExist() {
        // Given
        String characterName = "마볼링";
        TodoContentName todoContentName = TodoContentName.상아탑_하드;
        CharacterTodoDto characterTodoDto = CharacterTodoDto.builder()
                .characterName(characterName)
                .contentName(todoContentName)
                .build();

        Character character = new Character();
        character.setCharacterName(characterName);
        character.setTodoList(new ArrayList<>());

        Todo todo = Todo.builder()
                .contentName(todoContentName)
                .character(character)
                .build();
        character.getTodoList().add(todo);

        int gold = 1000;

        // 해당 컨텐츠가 캐릭터에 이미 존재함
        when(todoRepository.findByCharacterAndContentName(eq(character), eq(todoContentName)))
                .thenReturn(todo);

        // When
        Throwable throwable = catchThrowable(() -> todoService.updateWeek(characterTodoDto, character, gold));

        // Then
        assertThat(throwable).isNull(); //예외 발생X
        verify(todoRepository, times(1)).delete(any()); //delete 메서드 한번 호출
        verify(todoRepository, never()).save(any()); //save 메서드 호출X
    }

    @Test
    @DisplayName("updateWeek 성공(같은 레이드 카테고리일때 교체)")
    void updateWeekChange() {
        // Given
        String characterName = "마볼링";
        TodoContentName todoContentName = TodoContentName.상아탑_하드;
        CharacterTodoDto characterTodoDto = CharacterTodoDto.builder()
                .characterName(characterName)
                .contentName(todoContentName)
                .build();

        Character character = new Character();
        character.setCharacterName(characterName);
        character.setTodoList(new ArrayList<>());

        Todo existTodo = Todo.builder()
                .contentName(TodoContentName.상아탑_노말)
                .character(character)
                .build();
        character.getTodoList().add(existTodo);

        int gold = 1000;

        // 해당 컨텐츠가 캐릭터에 저장되어 있지 않음
        when(todoRepository.findByCharacterAndContentName(eq(character), eq(todoContentName)))
                .thenReturn(null);

        // When
        Throwable throwable = catchThrowable(() -> todoService.updateWeek(characterTodoDto, character, gold));

        // Then
        assertThat(throwable).isNull(); //예외 발생X
        verify(todoRepository, never()).delete(any()); //delete 메서드 호출X
        verify(todoRepository, never()).save(any()); //save 메서드 호춡X
    }
}






