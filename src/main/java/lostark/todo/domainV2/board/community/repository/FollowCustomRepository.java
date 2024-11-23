package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.member.entity.Member;
import lostark.todo.domainV2.board.community.entity.Follow;

import java.util.List;
import java.util.Optional;

public interface FollowCustomRepository {
    List<Follow> search(String username);

    Optional<Follow> get(Member follower, Member following);
}
