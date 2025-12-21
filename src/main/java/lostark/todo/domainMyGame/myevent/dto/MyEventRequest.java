package lostark.todo.domainMyGame.myevent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import lostark.todo.domainMyGame.myevent.enums.MyEventType;
import lostark.todo.domainMyGame.mygame.entity.MyGame;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyEventRequest {

    @NotNull(message = "게임 ID는 필수입니다.")
    private Long gameId;

    @NotBlank(message = "이벤트 제목은 필수입니다.")
    @Size(max = 200, message = "이벤트 제목은 200자를 초과할 수 없습니다.")
    private String title;

    @Size(max = 2000, message = "설명은 2000자를 초과할 수 없습니다.")
    private String description;

    private MyEventType type;

    @NotNull(message = "시작 날짜는 필수입니다.")
    private LocalDateTime startDate;

    @NotNull(message = "종료 날짜는 필수입니다.")
    private LocalDateTime endDate;

    @Size(max = 500, message = "이미지 URL은 500자를 초과할 수 없습니다.")
    private String image;

    public MyEvent toEntity(MyGame game) {
        return MyEvent.builder()
                .game(game)
                .title(title)
                .description(description)
                .type(type)
                .startDate(startDate)
                .endDate(endDate)
                .image(image)
                .build();
    }
}
