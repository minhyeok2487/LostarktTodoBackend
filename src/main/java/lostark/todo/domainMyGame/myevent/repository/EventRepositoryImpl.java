package lostark.todo.domainMyGame.myevent.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import lostark.todo.domainMyGame.myevent.enums.MyEventType;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static lostark.todo.domainMyGame.myevent.entity.QMyEvent.myEvent;
import static lostark.todo.domainMyGame.mygame.entity.QMyGame.myGame;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public MyEvent get(Long id) {
        return Optional.ofNullable(
                factory.selectFrom(myEvent)
                        .leftJoin(myEvent.game, myGame).fetchJoin()
                        .where(myEvent.id.eq(id))
                        .fetchOne()
        ).orElseThrow(() -> new ConditionNotMetException("이벤트를 찾을 수 없습니다."));
    }

    @Override
    public PageImpl<MyEvent> searchEvents(List<Long> gameIds, LocalDateTime startDate,
                                          LocalDateTime endDate, MyEventType type, PageRequest pageRequest) {
        List<MyEvent> events = factory.selectFrom(myEvent)
                .leftJoin(myEvent.game, myGame).fetchJoin()
                .where(
                        inGameIds(gameIds),
                        goeStartDate(startDate),
                        loeEndDate(endDate),
                        eqType(type)
                )
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(myEvent.startDate.asc())
                .fetch();

        long total = factory.selectFrom(myEvent)
                .where(
                        inGameIds(gameIds),
                        goeStartDate(startDate),
                        loeEndDate(endDate),
                        eqType(type)
                )
                .fetchCount();

        return new PageImpl<>(events, pageRequest, total);
    }

    private BooleanExpression inGameIds(List<Long> gameIds) {
        if (gameIds != null && !gameIds.isEmpty()) {
            return myEvent.game.id.in(gameIds);
        }
        return null;
    }

    private BooleanExpression goeStartDate(LocalDateTime startDate) {
        if (startDate != null) {
            return myEvent.startDate.goe(startDate);
        }
        return null;
    }

    private BooleanExpression loeEndDate(LocalDateTime endDate) {
        if (endDate != null) {
            return myEvent.endDate.loe(endDate);
        }
        return null;
    }

    private BooleanExpression eqType(MyEventType type) {
        if (type != null) {
            return myEvent.type.eq(type);
        }
        return null;
    }
}
