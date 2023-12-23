package lostark.todo.controller.dto.noticesDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.boardsDto.BoardResponseDto;
import lostark.todo.controller.dto.boardsDto.BoardsDto;
import lostark.todo.domain.notices.Notices;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticesDto {

    @ApiModelProperty(example = "로스트아크 공지사항 리스트")
    private List<Notices> noticesList;

    @ApiModelProperty(example = "토탈 페이지")
    private int totalPages;

    @ApiModelProperty(example = "현재 인덱스")
    private int page;

    public NoticesDto toDto(List<Notices> noticesList, int totalPages, int page) {
        return NoticesDto.builder()
                .noticesList(noticesList)
                .totalPages(totalPages)
                .page(page)
                .build();
    }
}
