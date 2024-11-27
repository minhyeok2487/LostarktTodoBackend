package lostark.todo.domain.member.repository;

import lostark.todo.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    Boolean existsByUsername(String username);
}
