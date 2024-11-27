package lostark.todo.domain.notification.repository;

import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationCustomRepository {

    List<Notification> searchBoard(Member member);

    List<Notification> search(Member member);

    Optional<Notification> get(long notificationId, String username);

    LocalDateTime getRecent(String username);

    long getUnreadCount(String username);

    void updateReadAll(Member member);
}
