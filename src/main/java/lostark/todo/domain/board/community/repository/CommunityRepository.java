package lostark.todo.domain.board.community.repository;

import lostark.todo.domain.board.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long>, CommunityCustomRepository{
}
