package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.board.community.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
