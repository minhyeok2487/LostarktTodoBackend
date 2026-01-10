package lostark.todo.domain.servertodo.repository;

import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.servertodo.entity.ServerTodoState;
import lostark.todo.domain.servertodo.enums.VisibleWeekday;

import java.util.List;

public interface ServerTodoStateRepositoryCustom {

    List<ServerTodoState> findByMemberAndServerNames(Long memberId, List<String> serverNames);

    ServerTodoState findByMemberAndTodo(Long memberId, Long todoId, String serverName);

    // 특정 요일에 해당하는 관리자 생성 숙제만 리셋 (frequency가 null인 것)
    long resetByVisibleWeekday(VisibleWeekday weekday);

    // 사용자 생성 숙제 중 해당 frequency인 것만 리셋
    long resetByFrequency(CustomTodoFrequencyEnum frequency);
}
