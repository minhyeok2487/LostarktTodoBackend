package lostark.todo.controller.dto.boardsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListDto {

    private List<BoardDto> boardDtoList;
    private int totalPages;

    private List<BoardDto> noticeList;
}
