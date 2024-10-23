package lostark.todo.domainV2.board.community.dto;

import lombok.Data;
import lostark.todo.domainV2.board.community.entity.CommunityCategory;

import java.time.LocalDateTime;

@Data
public class CommunityResponse {

    private long communityId;

    private LocalDateTime createdDate;

    private String name;

    private String body;

    private CommunityCategory category;

    private boolean myPost;

    private int likeCount;

    private boolean myLike;

    private long commentCount;
}
