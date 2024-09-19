package lostark.todo.controller.dto.commentsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentListDto {

    List<CommentResponseDto> commentDtoList;

    int totalPages;
}
