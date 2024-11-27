package lostark.todo.domain.friend.repository;

import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.friend.entity.Friends;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Long>, FriendsCustomRepository {

    Friends findByMemberAndFromMember(Member member, long fromMember);

    List<Friends> findAllByFromMember(long id);

    Optional<Friends> findByMemberAndId(Member member, long friendId);
}
