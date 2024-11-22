package lostark.todo.domainV2.member.repository;

import lostark.todo.domainV2.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    Boolean existsByUsername(String username);
}
