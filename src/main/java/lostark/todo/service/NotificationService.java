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
                .relatedUrl("/boards/" + boardId)
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
                .relatedUrl("/comments/" + comments.getId())
                .isRead(false)
                .notificationType(NotificationType.COMMENT)
                .commentId(comments.getId())
                .receiver(comments.getMember())
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void saveAddFriendRequest(Member toMember, Member fromMember) {
        String fromCharacterName = getMainCharacterName(fromMember);
        String toCharacterName = getMainCharacterName(toMember);

        createAndSaveNotification(toMember, fromCharacterName + "님에게 깐부요청중 이에요.", "/friends/" + toMember.getId() + "/" + fromMember.getId());
        createAndSaveNotification(fromMember, toCharacterName + "님이 깐부요청을 보냈어요.", "/friends/" + fromMember.getId() + "/" + toMember.getId());
    }

    @Transactional
    public void saveUpdateFriendRequestOk(Member toMember, Member fromMember) {
        String toCharacterName = getMainCharacterName(toMember);
        createAndSaveNotification(fromMember, toCharacterName + "님이 깐부요청을 수락했어요.", "/friends/" + fromMember.getId() + "/" + toMember.getId() + "/ok");
    }

    @Transactional
    public void saveUpdateFriendRequestReject(Member toMember, Member fromMember) {
        String toCharacterName = getMainCharacterName(toMember);
        createAndSaveNotification(fromMember, toCharacterName + "님이 깐부요청을 거절했어요.", "/friends/" + fromMember.getId() + "/" + toMember.getId() + "/reject");
    }

    private String getMainCharacterName(Member member) {
        return member.getMainCharacter() != null ? member.getMainCharacter() : member.getCharacters().get(0).getCharacterName();
    }

    private void createAndSaveNotification(Member receiver, String content, String relatedUrl) {
        Notification notification = Notification.builder()
                .content(content)
                .relatedUrl(relatedUrl)
                .isRead(false)
                .notificationType(NotificationType.FRIEND)
                .receiver(receiver)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public Notification updateRead(long notificationId, String username) {
        Notification notification = notificationRepository.get(notificationId, username).orElseThrow(() -> new IllegalArgumentException("없는 알림 입니다."));
        notification.updateRead();
        return notification;
    }
}
