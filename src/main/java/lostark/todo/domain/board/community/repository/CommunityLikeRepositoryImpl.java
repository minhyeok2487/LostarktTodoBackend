package lostark.todo.domain.board.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static lostark.todo.domain.board.community.entity.QCommunityLike.communityLike;

@RequiredArgsConstructor
public class CommunityLikeRepositoryImpl implements CommunityLikeCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public void deleteByIdSafe(Long id) {
        factory.delete(communityLike)
                .where(communityLike.id.eq(id))
                .execute();
    }
}
