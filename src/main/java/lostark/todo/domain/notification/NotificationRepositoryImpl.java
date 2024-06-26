package lostark.todo.domain.notification;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.notification.GetNotificationRequest;
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
                        eqUsername(member.getUsername())
                )
                .orderBy(notification.createdDate.desc())
                .fetch();
    }

    @Override
    public Notification get(GetNotificationRequest request) {
        return factory.selectFrom(notification)
                .leftJoin(notification.receiver, QMember.member).fetchJoin()
                .where(
                        eqUsername(request.getUsername()),
                        eqType(request.getNotificationType()),
                        eqBoardId(request.getBoardId())
                )
                .orderBy(notification.createdDate.desc())
                .fetchOne();
    }

    private BooleanExpression betweenDate(LocalDateTime beforeDate, LocalDateTime afterDate) {
        return notification.createdDate.between(beforeDate, afterDate);
    }

    private BooleanExpression eqUsername(String username) {
        return notification.receiver.username.eq(username);
    }

    private BooleanExpression eqType(NotificationType type) {
        return notification.notificationType.eq(type);
    }

    private BooleanExpression eqBoardId(long boardId) {
        return notification.boardId.eq(boardId);
    }

}
