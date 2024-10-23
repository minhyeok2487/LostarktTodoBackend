package lostark.todo.domainV2.board.community.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.dto.CommunityResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.repository.CommunityRepository;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional
public class CommunityDao {

    private final CommunityRepository repository;

    @Transactional(readOnly = true)
    public CursorResponse<CommunityResponse> search(long memberId, CommunitySearchParams params, PageRequest pageRequest) {
        return repository.search(memberId, params, pageRequest);
    }
}
