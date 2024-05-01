package lostark.todo.domain.friends;

import lostark.todo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Long> {

    boolean existsByMemberAndFromMember(Member member, long fromMember);
    Friends findByMemberAndFromMember(Member member, long fromMember);

    List<Friends> findAllByMember(Member member);

    List<Friends> findAllByFromMember(long id);

    @Modifying
    @Query(value = "DELETE FROM Friends f WHERE f.member = :member OR f.fromMember = :memberId")
    int deleteByMember(@Param("member") Member member, @Param("memberId") long memberId);

    @Query(value = "SELECT f FROM Friends f WHERE f.member.id = :memberId AND f.fromMember = :fromMemberId")
    Optional<Friends> findFriend(long memberId, long fromMemberId);
}
