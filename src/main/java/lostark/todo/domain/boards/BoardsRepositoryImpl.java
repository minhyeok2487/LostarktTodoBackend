package lostark.todo.domain.boards;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static lostark.todo.domain.boards.QBoards.boards;

@RequiredArgsConstructor
public class BoardsRepositoryImpl implements BoardsCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public List<Boards> search() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        return factory.selectFrom(boards)
                .where(
                        betweenDate(oneMonthAgo, now)
                )
                .orderBy(boards.createdDate.asc())
                .fetch();

    }

    private BooleanExpression betweenDate(LocalDateTime beforeDate, LocalDateTime afterDate) {
        return boards.createdDate.between(beforeDate, afterDate);
    }
}
