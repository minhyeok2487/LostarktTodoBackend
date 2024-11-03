package lostark.todo.domainV2.board.community.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.entity.CommunityLike;
import lostark.todo.domainV2.board.community.repository.CommunityLikeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@RequiredArgsConstructor
@Repository
@Transactional
public class CommunityLikeDao {

    private final CommunityLikeRepository repository;

    @Transactional
    public void updateLike(long memberId, long communityId) {
        Optional<CommunityLike> communityLike = repository.findByCommunityIdAndMemberId(communityId, memberId);
        if (communityLike.isPresent()) {
            repository.delete(communityLike.get());
        } else {
            repository.save(CommunityLike.builder().communityId(communityId).memberId(memberId).build());
        }
    }
}
