package lostark.todo.domain.board.community.repository;

import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.board.community.entity.Follow;

import java.util.List;
import java.util.Optional;

public interface FollowCustomRepository {
    List<Follow> search(String username);

    Optional<Follow> get(Member follower, Member following);
}
