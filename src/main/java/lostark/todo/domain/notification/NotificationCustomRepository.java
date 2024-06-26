package lostark.todo.domain.notification;

import lostark.todo.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface NotificationCustomRepository {

    List<Notification> search(Member member);

    Optional<Notification> get(long notificationId, String username);
}
