package lostark.todo.controller.dto.commentsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {

    private long id;

    private String body;

    private long parentId;
}
