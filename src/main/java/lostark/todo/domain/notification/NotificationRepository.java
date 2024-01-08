package lostark.todo.domain.notification;

import lostark.todo.domain.notices.Notices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = "SELECT n FROM Notification n WHERE n.receiver.username = :username AND n.isRead = :isRead")
    List<Notification> findAllByUsernameAndIsRead(String username, boolean isRead);
}
