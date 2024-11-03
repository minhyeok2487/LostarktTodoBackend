package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.board.community.entity.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long>{

    Optional<CommunityLike> findByCommunityIdAndMemberId(long communityId, long memberId);
}
