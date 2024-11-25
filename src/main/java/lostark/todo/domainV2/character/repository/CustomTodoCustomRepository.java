package lostark.todo.domainV2.character.repository;

import lostark.todo.domainV2.character.entity.CustomTodo;
import lostark.todo.domainV2.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domainV2.member.entity.Member;

import java.util.List;

public interface CustomTodoCustomRepository {

    CustomTodo get(Long customTodoId, Long characterId);

    List<CustomTodo> search(String username);

    long update(CustomTodoFrequencyEnum frequency);

    void deleteByMember(Member member);
}
