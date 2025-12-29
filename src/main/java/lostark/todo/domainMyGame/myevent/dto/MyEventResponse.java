package lostark.todo.domainMyGame.myevent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import lostark.todo.domainMyGame.myevent.entity.MyEventImage;
import lostark.todo.domainMyGame.myevent.entity.MyEventVideo;
import lostark.todo.domainMyGame.myevent.enums.MyEventType;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<String> images;
    private List<String> videos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyEventResponse from(MyEvent event) {
        List<String> imageUrls = event.getImages().stream()
                .sorted(Comparator.comparing(MyEventImage::getOrdering))
                .map(MyEventImage::getUrl)
                .collect(Collectors.toList());

        List<String> videoUrls = event.getVideos().stream()
                .sorted(Comparator.comparing(MyEventVideo::getOrdering))
                .map(MyEventVideo::getUrl)
                .collect(Collectors.toList());

        return MyEventResponse.builder()
                .id(event.getId())
                .gameId(event.getGame() != null ? event.getGame().getId() : null)
                .title(event.getTitle())
                .description(event.getDescription())
                .type(event.getType())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .images(imageUrls)
                .videos(videoUrls)
                .createdAt(event.getCreatedDate())
                .updatedAt(event.getLastModifiedDate())
                .build();
    }
}
