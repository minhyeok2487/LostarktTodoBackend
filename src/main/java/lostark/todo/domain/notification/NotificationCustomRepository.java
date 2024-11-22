package lostark.todo.domain.notification;

import lostark.todo.domainV2.member.entity.Member;

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
