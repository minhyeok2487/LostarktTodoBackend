package lostark.todo.domain.notification.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lostark.todo.domain.notification.enums.NotificationType;
import lostark.todo.global.entity.BaseTimeEntity;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.board.community.entity.Community;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private long id;

    private String content;

    private boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    private long boardId;

    private long commentId;

    private long friendId;

    private String friendUsername;

    private String friendCharacterName;

    private long communityId;

    private long inspectionCharacterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member receiver;

    public void updateRead() {
        this.isRead = true;
    }

    public static Notification createReplyNotification(Community rootCommunity, Member receiver) {
        return builder()
                .content("새로운 답글이 달렸습니다.")
                .isRead(false)
                .notificationType(NotificationType.COMMUNITY)
                .communityId(rootCommunity.getId())
                .receiver(receiver)
                .build();
    }
}
