package lostark.todo.controller.dto.boardsDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.member.Member;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDto {

    private long id;

    @ApiModelProperty(notes = "글 작성자(이메일)")
    private String writer;

    @ApiModelProperty(notes = "제목")
    private String title;

    @ApiModelProperty(notes = "내용")
    private String content;

    @ApiModelProperty(notes = "조회수")
    private int views;

    @ApiModelProperty(notes = "작성일")
    private LocalDateTime regDate;

    public BoardResponseDto toDto(Boards board) {
        return BoardResponseDto.builder()
                .id(board.getId())
                .writer(board.getMember().getUsername())
                .title(board.getTitle())
                .content(board.getContent().replaceAll("\r\n|\n|\r", "<br />"))
                .views(board.getViews())
                .regDate(board.getCreatedDate())
                .build();
    }

    public Boards toEntityDefault(BoardResponseDto boardResponseDto, Member member) {
        return Boards.builder()
                .member(member)
                .title(boardResponseDto.getTitle())
                .content(boardResponseDto.getContent())
                .views(0)
                .build();
    }
}
