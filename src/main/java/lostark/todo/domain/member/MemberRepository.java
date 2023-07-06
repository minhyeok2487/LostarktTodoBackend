package lostark.todo.domain.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = {"characters"})
    Member findByUsername(String username);

    @Query(value = "SELECT DISTINCT m FROM Member m JOIN FETCH m.characters c WHERE c.selected = true ORDER BY c.itemLevel DESC")
    Member findByUsernameSelected(String username);

}
