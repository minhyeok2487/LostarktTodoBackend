package lostark.todo.domain.admin.dto;

import lombok.Builder;
import lombok.Data;
import lostark.todo.domain.board.comments.entity.Comments;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminCommentResponse {

    private long id;
    private String body;
    private long memberId;
    private String memberUsername;
    private long parentId;
    private LocalDateTime createdDate;

    public static AdminCommentResponse from(Comments comment) {
        return AdminCommentResponse.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .memberId(comment.getMember() != null ? comment.getMember().getId() : 0)
                .memberUsername(comment.getMember() != null ? comment.getMember().getUsername() : null)
                .parentId(comment.getParentId())
                .createdDate(comment.getCreatedDate())
                .build();
    }
}
