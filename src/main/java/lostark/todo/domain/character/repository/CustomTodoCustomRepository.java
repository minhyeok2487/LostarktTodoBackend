package lostark.todo.domain.character.repository;

import lostark.todo.controller.dtoV2.character.CustomTodoResponse;
import lostark.todo.domain.character.entity.CustomTodo;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.member.entity.Member;

import java.util.List;

public interface CustomTodoCustomRepository {

    CustomTodo get(Long customTodoId, Long characterId);

    List<CustomTodo> search(String username);

    List<CustomTodoResponse> searchResponse(String username);

    long update(CustomTodoFrequencyEnum frequency);

    void deleteByMember(Member member);
}
