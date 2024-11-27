package lostark.todo.domain.board.community.repository;

import lostark.todo.domain.board.community.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowCustomRepository {
}
