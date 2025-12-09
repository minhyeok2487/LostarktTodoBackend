package lostark.todo.domain.servertodo.dto;

import lombok.Getter;
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

    private Set<VisibleWeekday> visibleWeekdays;
}
