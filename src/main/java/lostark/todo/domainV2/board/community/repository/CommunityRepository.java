package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.board.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long>, CommunityCustomRepository{
}
