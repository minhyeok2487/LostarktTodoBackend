package lostark.todo.domain.board.community.repository;

import lostark.todo.domain.board.community.dto.CommunityCommentResponse;
import lostark.todo.domain.board.community.dto.CommunitySearchResponse;
import lostark.todo.domain.board.community.dto.CommunitySearchParams;
import lostark.todo.domain.board.community.entity.Community;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CommunityCustomRepository {

    CursorResponse<CommunitySearchResponse> search(long memberId, CommunitySearchParams params, PageRequest pageRequest);

    Community get(String username, long communityId);

    CommunitySearchResponse getResponse(long memberId, long communityId);

    List<CommunityCommentResponse> getComments(long memberId, Long rootParentId);

    Community get(long communityId);
}
