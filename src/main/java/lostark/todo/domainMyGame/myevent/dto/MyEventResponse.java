package lostark.todo.domainMyGame.myevent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import lostark.todo.domainMyGame.myevent.enums.MyEventType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyEventResponse {

    private Long id;
    private Long gameId;
    private String title;
    private String description;
    private MyEventType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyEventResponse from(MyEvent event) {
        return MyEventResponse.builder()
                .id(event.getId())
                .gameId(event.getGame() != null ? event.getGame().getId() : null)
                .title(event.getTitle())
                .description(event.getDescription())
                .type(event.getType())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .image(event.getImage())
                .createdAt(event.getCreatedDate())
                .updatedAt(event.getLastModifiedDate())
                .build();
    }
}
