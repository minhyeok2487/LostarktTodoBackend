package lostark.todo.domain.board.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.member.enums.Role;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long commentId;

    private String body;

    private String username;

    private LocalDateTime regDate;

    private Long memberId;

    private Integer likeCount;

    private Long commentCount;

    private Role role;
}
