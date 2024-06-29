package lostark.todo.domain.schedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleCustomRepository {

    private final JPAQueryFactory factory;
}
