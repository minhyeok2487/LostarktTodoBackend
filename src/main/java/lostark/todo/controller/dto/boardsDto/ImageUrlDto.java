package lostark.todo.controller.dto.boardsDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dtoV2.image.ImageResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUrlDto {

    @ApiModelProperty(notes = "url")
    private String url;

    @ApiModelProperty(notes = "fileName")
    private String fileName;

    public ImageUrlDto(ImageResponse imageResponse) {
        this.fileName = imageResponse.getFileName();
        this.url = imageResponse.getImageUrl();
    }
}
