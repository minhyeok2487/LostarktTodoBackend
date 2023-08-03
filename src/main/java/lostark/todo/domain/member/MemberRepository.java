package lostark.todo.domain.member;

import lostark.todo.controller.v1.dto.memberDto.MemberRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    @Query(value = "SELECT DISTINCT m FROM Member m JOIN FETCH m.characters c WHERE m.username = :username ORDER BY c.itemLevel DESC")
    Optional<Member> findByUsername(@Param("username") String username);

    Optional<Member> findByUsernameAndPassword(String username, String password);
}
