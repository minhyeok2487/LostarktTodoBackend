package lostark.todo.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.admin.dto.AdminNotificationResponse;
import lostark.todo.domain.notification.dto.NotificationStatusResponse;
import lostark.todo.domain.board.community.entity.Community;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.notification.entity.Notification;
import lostark.todo.domain.notification.repository.NotificationRepository;
import lostark.todo.domain.notification.enums.NotificationType;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final MemberRepository memberRepository;

    @Transactional
    public List<Notification> search(Member member, List<Community> boards) {
        List<Notification> notifications = notificationRepository.searchBoard(member);

        for (Community board : boards) {
            if (!notifications.stream().map(Notification::getBoardId).toList().contains(board.getId())) {
                notifications.add(createBoardNotification(board.getId(), member));
            }
        }

        List<Notification> search = notificationRepository.search(member);
        notifications.addAll(search);

        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getCreatedDate).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Notification> search(Member member) {

        return notificationRepository.search(member).stream()
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
        return member.getMainCharacterName() != null ? member.getMainCharacterName() : member.getCharacters().get(0).getCharacterName();
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
        Notification notification = notificationRepository.get(notificationId, username).orElseThrow(() -> new ConditionNotMetException("없는 알림 입니다."));
        notification.updateRead();
    }

    @Transactional(readOnly = true)
    public NotificationStatusResponse getStatus(String username) {
        LocalDateTime latestCreatedDate = notificationRepository.getRecent(username);
        long unreadCount = notificationRepository.getUnreadCount(username);
        return new NotificationStatusResponse(latestCreatedDate, unreadCount);
    }

    public void updateReadAll(Member member) {
        notificationRepository.updateReadAll(member);
    }

    // =============== Admin Methods ===============

    @Transactional(readOnly = true)
    public Page<AdminNotificationResponse> getNotificationsForAdmin(Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "createdDate")));

        return notifications.map(AdminNotificationResponse::from);
    }

    @Transactional
    public int broadcast(String content) {
        List<Member> allMembers = memberRepository.findAll();
        int count = 0;

        for (Member member : allMembers) {
            Notification notification = Notification.builder()
                    .content(content)
                    .isRead(false)
                    .notificationType(NotificationType.BOARD)
                    .receiver(member)
                    .build();
            notificationRepository.save(notification);
            count++;
        }

        return count;
    }

    @Transactional
    public void deleteByAdmin(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ConditionNotMetException("알림이 존재하지 않습니다. ID: " + notificationId));
        notificationRepository.delete(notification);
    }
}
