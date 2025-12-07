package lostark.todo.domainMyGame.mygame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainMyGame.mygame.entity.MyGame;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyGameResponse {

    private String id;
    private String name;
    private String image;
    private String color;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyGameResponse from(MyGame game) {
        return MyGameResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .image(game.getImage())
                .color(game.getColor())
                .description(game.getDescription())
                .createdAt(game.getCreatedDate())
                .updatedAt(game.getLastModifiedDate())
                .build();
    }
}
