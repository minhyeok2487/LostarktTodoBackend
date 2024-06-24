package lostark.todo.domain.notification;

import lostark.todo.domain.member.Member;

import java.util.List;

public interface NotificationCustomRepository {

    List<Notification> search(Member member);
}
