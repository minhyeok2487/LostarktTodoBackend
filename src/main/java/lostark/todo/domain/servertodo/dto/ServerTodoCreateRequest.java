package lostark.todo.domain.servertodo.dto;

import lombok.Getter;
import lostark.todo.domain.character.enums.CustomTodoFrequencyEnum;
import lostark.todo.domain.servertodo.enums.VisibleWeekday;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
public class ServerTodoCreateRequest {

    @NotBlank(message = "숙제 이름은 필수입니다.")
    private String contentName;

    @NotNull(message = "기본 활성화 여부는 필수입니다.")
    private Boolean defaultEnabled;

    // 관리자용: 초기화 요일 (frequency가 null일 때 사용)
    private Set<VisibleWeekday> visibleWeekdays;

    // 사용자용: 초기화 주기 (DAILY 또는 WEEKLY)
    private CustomTodoFrequencyEnum frequency;

    // true면 사용자 생성, false면 관리자 생성
    private boolean custom = false;
}
