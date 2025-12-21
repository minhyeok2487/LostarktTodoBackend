package lostark.todo.domainMyGame.mygame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domainMyGame.mygame.entity.MyGame;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyGameRequest {

    @NotBlank(message = "게임 이름은 필수입니다.")
    @Size(max = 200, message = "게임 이름은 200자를 초과할 수 없습니다.")
    private String name;

    @Size(max = 500, message = "이미지 URL은 500자를 초과할 수 없습니다.")
    private String image;

    @NotBlank(message = "색상은 필수입니다.")
    @Size(max = 20, message = "색상은 20자를 초과할 수 없습니다.")
    private String color;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
    private String description;

    public MyGame toEntity() {
        return MyGame.builder()
                .name(name)
                .image(image)
                .color(color)
                .description(description)
                .build();
    }
}
