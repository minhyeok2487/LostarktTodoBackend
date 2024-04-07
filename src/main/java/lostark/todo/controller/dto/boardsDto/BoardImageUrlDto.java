package lostark.todo.controller.dto.boardsDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.boards.BoardImages;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardImageUrlDto {

    @ApiModelProperty(notes = "url")
    private String url;

    @ApiModelProperty(notes = "fileName")
    private String fileName;

    public BoardImageUrlDto(BoardImages upload) {
        this.fileName = upload.getFileName();
        this.url = upload.getImageUrl();
    }
}
