package lostark.todo.domain.notification;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.QMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                        eqUsername(member.getUsername())
                )
                .orderBy(notification.createdDate.desc())
                .fetch();
    }

    @Override
    public Optional<Notification> get(long notificationId, String username) {
        Notification result = factory.selectFrom(notification)
                .leftJoin(notification.receiver, QMember.member).fetchJoin()
                .where(
                        eqId(notificationId),
                        eqUsername(username)
                )
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public LocalDateTime getRecent(String username) {
        return factory.select(notification.createdDate)
                .from(notification)
                .where(
                        eqUsername(username)
                )
                .orderBy(notification.createdDate.desc())
                .fetchFirst();
    }

    private BooleanExpression betweenDate(LocalDateTime beforeDate, LocalDateTime afterDate) {
        return notification.createdDate.between(beforeDate, afterDate);
    }

    private BooleanExpression eqId(long id) {
        return notification.id.eq(id);
    }


    private BooleanExpression eqUsername(String username) {
        return notification.receiver.username.eq(username);
    }

}
