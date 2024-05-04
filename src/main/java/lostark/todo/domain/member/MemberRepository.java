package lostark.todo.domain.member;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    List<Member> findAllByApiKeyNotNull();

    Boolean existsByUsername(String username);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.characters c " +
            "WHERE c.member.username = :username")
    Optional<Member> findByUsername(String username);
}
