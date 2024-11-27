package lostark.todo.domain.board.community.dto;

import lombok.Data;
import java.util.List;

@Data
public class CommunityGetResponse {

    private CommunitySearchResponse community;

    private List<CommunityCommentResponse> comments;

    public CommunityGetResponse(CommunitySearchResponse searchResponse, List<CommunityCommentResponse> commentResponseList) {
        this.community = searchResponse;
        this.comments = commentResponseList;
    }
}
