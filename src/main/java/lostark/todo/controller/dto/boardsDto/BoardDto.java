package lostark.todo.controller.dto.boardsDto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {

    private long id;

    private String writer;

    private String title;

    private String content;

    private boolean isNotice;

    private int views;

    private LocalDateTime regDate;

    public BoardDto toDto(Boards board) {
        return BoardDto.builder()
                .id(board.getId())
                .writer(board.getMember().getUsername())
                .title(board.getTitle())
                .content(board.getContent().replaceAll("\r\n|\n|\r", "<br />"))
                .isNotice(board.isNotice())
                .views(board.getViews())
                .regDate(board.getCreatedDate())
                .build();
    }

    public Boards toEntityDefault(BoardDto boardDto, Member member) {
        return Boards.builder()
                .member(member)
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .isNotice(false)
                .views(0)
                .build();
    }
}
