package lostark.todo.domain.notification;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.QMember;

import java.time.LocalDateTime;
import java.util.List;

import static lostark.todo.domain.notification.QNotification.notification;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public List<Notification> search(Member member) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        return factory.selectFrom(notification)
                .leftJoin(notification.receiver, QMember.member).fetchJoin()
                .where(
                        betweenDate(oneMonthAgo, now),
                        eqUsername(member)
                )
                .orderBy(notification.createdDate.desc())
                .fetch();
    }

    private BooleanExpression betweenDate(LocalDateTime beforeDate, LocalDateTime afterDate) {
        return notification.createdDate.between(beforeDate, afterDate);
    }

    private BooleanExpression eqUsername(Member member) {
        return notification.receiver.eq(member);
    }
}
