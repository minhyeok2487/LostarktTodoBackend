package lostark.todo.domainV2.board.community.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.member.entity.Member;
import lostark.todo.domainV2.board.community.entity.Follow;
import lostark.todo.domainV2.board.community.repository.FollowRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class FollowDao {

    private final FollowRepository repository;

    @Transactional(readOnly = true)
    public List<Follow> search(String username) {
        return repository.search(username);
    }

    @Transactional
    public void update(Member follower, Member following) {
        repository.get(follower, following)
                .ifPresentOrElse(
                        repository::delete,  // 존재하면 삭제
                        () -> repository.save(Follow.builder()  // 없으면 생성
                                .follower(follower)
                                .following(following)
                                .build())
                );
    }
}
