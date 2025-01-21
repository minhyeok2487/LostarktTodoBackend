package lostark.todo.domain.character.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import static lostark.todo.domain.character.entity.QTodoV2.todoV2;

@RequiredArgsConstructor
public class TodoV2RepositoryImpl implements TodoV2CustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public void resetTodoV2() {
        factory.update(todoV2)
                .set(todoV2.isChecked, false)
                .set(todoV2.gold,
                        factory.select(todoV2.weekContent.gold)
                                .from(todoV2.weekContent)
                                .where(todoV2.weekContent.id.eq(todoV2.weekContent.id)))
                .set(todoV2.moreRewardCheck, false)
                .execute();
    }
}
