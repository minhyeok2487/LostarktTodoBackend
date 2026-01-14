package lostark.todo.domain.board.comments.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static lostark.todo.domain.board.comments.entity.QComments.comments;

@RequiredArgsConstructor
public class CommentsRepositoryImpl implements CommentsCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public void deleteByIdSafe(Long id) {
        factory.delete(comments)
                .where(comments.id.eq(id))
                .execute();
    }

    @Override
    public void deleteByParentIdSafe(Long parentId) {
        factory.delete(comments)
                .where(comments.parentId.eq(parentId))
                .execute();
    }
}
