package lostark.todo.domain.friends;

import lostark.todo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Long> {

    boolean existsByMemberAndFromMember(Member member, long fromMember);
    Friends findByMemberAndFromMember(Member member, long fromMember);

    List<Friends> findAllByMember(Member member);

    List<Friends> findAllByFromMember(long id);
}
