package lostark.todo.controller.dto.boardsDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardInsertDto {

    @ApiModelProperty(notes = "제목")
    private String title;

    @ApiModelProperty(notes = "내용")
    private String content;

    @ApiModelProperty(notes = "이미지 name 리스트")
    private List<String> fileNames;
}
