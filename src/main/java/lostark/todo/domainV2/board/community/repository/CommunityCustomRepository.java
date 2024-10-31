package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.board.community.dto.CommunityResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.entity.Community;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface CommunityCustomRepository {

    CursorResponse<CommunityResponse> search(long memberId, CommunitySearchParams params, PageRequest pageRequest);

    Optional<Community> get(String username, long communityId);
}
