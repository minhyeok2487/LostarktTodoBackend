package lostark.todo.global.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lostark.todo.controller.dtoV2.image.ImageResponse;

@Data
public class ImageResponseV2 {

    private long imageId;

    @ApiModelProperty(notes = "url")
    private String url;

    @ApiModelProperty(notes = "fileName")
    private String fileName;

    public ImageResponseV2(ImageResponse imageResponse, long id) {
        this.imageId = id;
        this.url = imageResponse.getImageUrl();
        this.fileName = imageResponse.getFileName();
    }
}
