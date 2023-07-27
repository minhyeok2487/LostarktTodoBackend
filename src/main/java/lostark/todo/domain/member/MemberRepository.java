package lostark.todo.domain.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    @Query(value = "SELECT DISTINCT m FROM Member m JOIN FETCH m.characters c WHERE m.username = :username ORDER BY c.itemLevel DESC")
    Optional<Member> findMemberAndCharacter(@Param("username") String username);

    Optional<Member> findByUsername(@Param("username") String username);
}
