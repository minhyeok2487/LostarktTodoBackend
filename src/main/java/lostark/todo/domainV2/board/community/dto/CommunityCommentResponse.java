package lostark.todo.domainV2.board.community.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityCommentResponse {

    private long commentId;

    private LocalDateTime createdDate;

    private String characterClassName;

    private String characterImage;

    private String name;

    private long memberId;

    private String body;

    private boolean myPost;

    private long likeCount;

    private boolean myLike;

    private long rootParentId;

    private long commentParentId;
}
