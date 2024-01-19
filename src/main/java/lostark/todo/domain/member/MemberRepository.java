package lostark.todo.domain.member;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByApiKeyNotNull();

    Boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);

    @Query(value = "SELECT m FROM Member m FETCH JOIN Characters c GROUP BY m.id ORDER BY COUNT(c.id) DESC LIMIT 20", nativeQuery = true)
    List<Member> findTop20ByCharactersCount(Pageable pageable);
}
