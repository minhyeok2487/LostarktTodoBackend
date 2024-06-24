package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.notification.Notification;
import lostark.todo.domain.notification.NotificationRepository;
import lostark.todo.domain.notification.NotificationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional()
    public List<Notification> search(Member member, List<Boards> searchBoard) {
        List<Notification> notifications = notificationRepository.search(member);

        for (Boards boards : searchBoard) {
            if (!notifications.stream().map(Notification::getBoardId).toList().contains(boards.getId())) {
                notifications.add(createBoardNotification(boards.getId(), member));
            }
        }
        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getCreatedDate).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public Notification createBoardNotification(long boardId, Member receiver) {
        Notification notification = Notification.builder()
                .content("읽지 않은 공지사항이 있어요!")
                .relatedUrl("/boards/"+boardId)
                .isRead(false)
                .notificationType(NotificationType.BOARD)
                .boardId(boardId)
                .receiver(receiver)
                .build();
        return notificationRepository.save(notification);
    }
}
