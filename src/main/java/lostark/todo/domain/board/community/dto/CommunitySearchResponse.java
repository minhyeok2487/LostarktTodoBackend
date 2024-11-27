package lostark.todo.domain.board.community.dto;

import lombok.Data;
import lostark.todo.domain.board.community.entity.CommunityCategory;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommunitySearchResponse {

    private long communityId;

    private LocalDateTime createdDate;

    private String characterClassName;

    private String characterImage;

    private String name;

    private long memberId;

    private String body;

    private CommunityCategory category;

    private boolean myPost;

    private long likeCount;

    private boolean myLike;

    private long commentCount;

    private List<String> imageList;
}
