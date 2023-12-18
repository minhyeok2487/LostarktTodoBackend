package lostark.todo.controller.dto.boardsDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardInsertDto {

    @ApiModelProperty(notes = "제목")
    private String title;

    @ApiModelProperty(notes = "내용")
    private String content;

}
