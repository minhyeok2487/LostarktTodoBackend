package lostark.todo.domain.board.community.repository;

import lostark.todo.domain.board.community.entity.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long>, CommunityLikeCustomRepository {

    Optional<CommunityLike> findByCommunityIdAndMemberId(long communityId, long memberId);
}
