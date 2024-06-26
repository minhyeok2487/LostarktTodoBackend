package lostark.todo.domain.notification;

import lostark.todo.controller.dtoV2.notification.GetNotificationRequest;
import lostark.todo.domain.member.Member;

import java.util.List;

public interface NotificationCustomRepository {

    List<Notification> search(Member member);

    Notification get(GetNotificationRequest request);
}
