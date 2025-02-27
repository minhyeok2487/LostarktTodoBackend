package lostark.todo.domain.notification.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.notification.enums.NotificationType;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.entity.QMember;
import lostark.todo.domain.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.notification.entity.QNotification.notification;


@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public List<Notification> searchBoard(Member member) {

        return factory.selectFrom(notification)
                .leftJoin(notification.receiver, QMember.member).fetchJoin()
                .where(
                        eqType(NotificationType.BOARD),
                        eqUsername(member.getUsername())
                )
                .orderBy(notification.createdDate.desc())
                .fetch();
    }

    @Override
    public List<Notification> search(Member member) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        return factory.selectFrom(notification)
                .leftJoin(notification.receiver, QMember.member).fetchJoin()
                .where(
                        betweenDate(oneMonthAgo, now),
                        eqUsername(member.getUsername()),
                        neType(NotificationType.BOARD)
                )
                .orderBy(notification.createdDate.desc())
                .fetch();
    }


    @Override
    public Optional<Notification> get(long notificationId, String username) {
        Notification result = factory.selectFrom(notification)
                .leftJoin(notification.receiver, member).fetchJoin()
                .where(
                        eqId(notificationId),
                        eqUsername(username)
                )
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public LocalDateTime getRecent(String username) {
        return factory.select(notification.createdDate.max())
                .from(notification)
                .where(
                        eqUsername(username)
                )
                .fetchFirst();
    }

    @Override
    public long getUnreadCount(String username) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        return Optional.ofNullable(factory.select(Wildcard.count)
                .from(notification)
                .where(
                        eqUsername(username),
                        betweenDate(oneMonthAgo, now),
                        eqUnread()
                )
                .fetchOne()).orElse(0L);
    }

    @Override
    public void updateReadAll(Member member) {
        factory.update(notification)
                .set(notification.isRead, true)
                .where(notification.receiver.eq(member))
                .execute();
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

    private BooleanExpression eqUnread() {
        return notification.isRead.eq(false);
    }

    private BooleanExpression eqType(NotificationType notificationType) {
        if (notificationType != null) {
            return notification.notificationType.eq(notificationType);
        }
        return null;
    }

    private BooleanExpression neType(NotificationType notificationType) {
        if (notificationType != null) {
            return notification.notificationType.ne(notificationType);
        }
        return null;
    }


}
