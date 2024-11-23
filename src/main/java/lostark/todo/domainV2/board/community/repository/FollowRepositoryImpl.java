package lostark.todo.domainV2.board.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.member.entity.Member;
import lostark.todo.domainV2.board.community.entity.Follow;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domainV2.member.entity.QMember.member;
import static lostark.todo.domainV2.board.community.entity.QFollow.follow;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<Follow> search(String username) {
        return factory.select(follow)
                .from(follow)
                .leftJoin(follow.follower, member).fetchJoin()
                .where(member.username.eq(username))
                .fetch();
    }

    @Override
    public Optional<Follow> get(Member follower, Member following) {
        return Optional.ofNullable(
                factory.select(follow)
                        .from(follow)
                        .leftJoin(follow.follower, member).fetchJoin()
                        .where(follow.follower.eq(follower).and(follow.following.eq(following)))
                        .fetchOne()
        );
    }
}
