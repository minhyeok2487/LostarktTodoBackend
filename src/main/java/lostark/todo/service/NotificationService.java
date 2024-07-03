package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.comments.Comments;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.notification.Notification;
import lostark.todo.domain.notification.NotificationRepository;
import lostark.todo.domain.notification.NotificationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public List<Notification> searchBoard(Member member, List<Boards> searchBoard) {
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
                .isRead(false)
                .notificationType(NotificationType.BOARD)
                .boardId(boardId)
                .receiver(receiver)
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional
    public void saveComment(Comments comments) {
        Notification notification = Notification.builder()
                .content("내가 쓴 방명록에 댓글이 달렸어요.")
                .isRead(false)
                .notificationType(NotificationType.COMMENT)
                .commentId(comments.getId())
                .receiver(comments.getMember())
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void saveAddFriendRequest(Member toMember, Member fromMember) {
        createAndSaveNotification(toMember, "님에게 깐부요청중 이에요.", fromMember);
        createAndSaveNotification(fromMember, "님이 깐부요청을 보냈어요.", toMember);
    }

    @Transactional
    public void saveUpdateFriendRequestOk(Member toMember, Member fromMember) {
        createAndSaveNotification(fromMember, "님이 깐부요청을 수락했어요.", toMember);
    }

    @Transactional
    public void saveUpdateFriendRequestReject(Member toMember, Member fromMember) {
        createAndSaveNotification(fromMember, "님이 깐부요청을 거절했어요.", toMember);
    }

    private String getMainCharacterName(Member member) {
        return member.getMainCharacter() != null ? member.getMainCharacter() : member.getCharacters().get(0).getCharacterName();
    }

    private void createAndSaveNotification(Member receiver, String content, Member friend) {
        Notification notification = Notification.builder()
                .content(content)
                .isRead(false)
                .notificationType(NotificationType.FRIEND)
                .friendId(friend.getId())
                .friendUsername(friend.getUsername())
                .friendCharacterName(getMainCharacterName(friend))
                .receiver(receiver)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void updateRead(long notificationId, String username) {
        Notification notification = notificationRepository.get(notificationId, username).orElseThrow(() -> new IllegalArgumentException("없는 알림 입니다."));
        notification.updateRead();
    }

    @Transactional(readOnly = true)
    public LocalDateTime getRecent(String username) {
        return notificationRepository.getRecent(username);
    }
}
