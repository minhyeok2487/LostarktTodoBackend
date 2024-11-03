package lostark.todo.domainV2.board.community.dao;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.dto.CommunityCommentResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.entity.Community;
import lostark.todo.domainV2.board.community.repository.CommunityRepository;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Transactional
public class CommunityDao {

    private final CommunityRepository repository;

    @Transactional(readOnly = true)
    public CursorResponse<CommunitySearchResponse> search(long memberId, CommunitySearchParams params, PageRequest pageRequest) {
        return repository.search(memberId, params, pageRequest);
    }

    @Transactional
    public Community save(Community community) {
        return repository.save(community);
    }

    @Transactional(readOnly = true)
    public Community get(String username, long communityId) {
        return repository.get(username, communityId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public CommunitySearchResponse getResponse(long memberId, long communityId) {
        return repository.getResponse(memberId, communityId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
    }

    public List<CommunityCommentResponse> getComments(long memberId, Long rootParentId) {
        return repository.getComments(memberId, rootParentId);
    }
}
