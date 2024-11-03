package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.board.community.dto.CommunityCommentResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.entity.Community;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CommunityCustomRepository {

    CursorResponse<CommunitySearchResponse> search(long memberId, CommunitySearchParams params, PageRequest pageRequest);

    Optional<Community> get(String username, long communityId);

    Optional<CommunitySearchResponse> getResponse(long memberId, long communityId);

    List<CommunityCommentResponse> getComments(long memberId, Long rootParentId);

}
