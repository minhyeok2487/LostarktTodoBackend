package lostark.todo.domain.customTodo;

import lostark.todo.domainV2.member.entity.Member;

import java.util.List;

public interface CustomTodoCustomRepository {

    List<CustomTodo> search(String username);

    long update(CustomTodoFrequencyEnum frequency);

    void deleteByMember(Member member);
}
