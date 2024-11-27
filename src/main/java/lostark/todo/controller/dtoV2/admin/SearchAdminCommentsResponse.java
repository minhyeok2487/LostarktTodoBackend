package lostark.todo.controller.dtoV2.admin;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lostark.todo.domain.board.comments.entity.Comments;

import java.time.LocalDateTime;

@Data
public class SearchAdminCommentsResponse {

    private long commentId;

    private String username;

    private LocalDateTime createdDate;

    private String body;

    @QueryProjection
    public SearchAdminCommentsResponse(Comments comments) {
        this.commentId = comments.getId();
        this.username = comments.getMember().getUsername();
        this.createdDate = comments.getCreatedDate();
        this.body = comments.getBody();
    }
}
