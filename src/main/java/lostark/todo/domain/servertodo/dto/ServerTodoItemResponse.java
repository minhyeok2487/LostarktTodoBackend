package lostark.todo.domain.servertodo.dto;

import lombok.Builder;
import lombok.Value;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.servertodo.entity.ServerTodo;
import lostark.todo.domain.servertodo.enums.VisibleWeekday;

import java.util.Set;

@Value
@Builder
public class ServerTodoItemResponse {
    Long todoId;
    String contentName;
    boolean defaultEnabled;
    Set<VisibleWeekday> visibleWeekdays;
    CustomTodoFrequencyEnum frequency;  // 사용자 생성 숙제의 초기화 주기
    boolean isCustom;                    // 사용자 생성 여부 (true: 사용자, false: 관리자)

    public static ServerTodoItemResponse from(ServerTodo serverTodo) {
        return ServerTodoItemResponse.builder()
                .todoId(serverTodo.getId())
                .contentName(serverTodo.getContentName())
                .defaultEnabled(serverTodo.isDefaultEnabled())
                .visibleWeekdays(Set.copyOf(serverTodo.getVisibleWeekdays()))
                .frequency(serverTodo.getFrequency())
                .isCustom(serverTodo.getMember() != null)
                .build();
    }
}
