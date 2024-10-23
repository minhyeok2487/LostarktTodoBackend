package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.board.community.dto.CommunityResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

public interface CommunityCustomRepository {

    CursorResponse<CommunityResponse> search(long memberId, CommunitySearchParams params, PageRequest pageRequest);
}
