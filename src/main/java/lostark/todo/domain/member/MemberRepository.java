package lostark.todo.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    @Query(value = "SELECT DISTINCT m FROM Member m JOIN FETCH m.characters c WHERE m.username = :username ORDER BY c.itemLevel DESC")
    Optional<Member> findByUsername(@Param("username") String username);

    int deleteByUsername(String username);
}
