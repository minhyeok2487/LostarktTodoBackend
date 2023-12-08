package lostark.todo.controller.dto.commentsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentListDto {

    List<CommentResponseDto> commentDtoList;

    int totalPages;

    MemberResponseDto memberResponseDto;
}
