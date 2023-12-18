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
public class BoardsDto {

    @ApiModelProperty(example = "사이트 공지사항 리스트")
    private List<BoardResponseDto> boardResponseDtoList;

    @ApiModelProperty(example = "토탈 페이지")
    private int totalPages;

    @ApiModelProperty(example = "현재 인덱스")
    private int page;

    public BoardsDto toDto(List<BoardResponseDto> boardResponseDtoList, int totalPages, int page) {
        return BoardsDto.builder()
                .boardResponseDtoList(boardResponseDtoList)
                .totalPages(totalPages)
                .page(page)
                .build();
    }
}
