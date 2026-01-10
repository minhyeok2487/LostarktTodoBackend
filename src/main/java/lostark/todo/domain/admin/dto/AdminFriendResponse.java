package lostark.todo.domain.admin.dto;

import lombok.Builder;
import lombok.Data;
import lostark.todo.domain.friend.entity.Friends;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminFriendResponse {

    private long id;
    private long memberId;
    private String memberUsername;
    private long fromMemberId;
    private boolean areWeFriend;
    private int ordering;
    private LocalDateTime createdDate;

    public static AdminFriendResponse from(Friends friend) {
        return AdminFriendResponse.builder()
                .id(friend.getId())
                .memberId(friend.getMember() != null ? friend.getMember().getId() : 0)
                .memberUsername(friend.getMember() != null ? friend.getMember().getUsername() : null)
                .fromMemberId(friend.getFromMember())
                .areWeFriend(friend.isAreWeFriend())
                .ordering(friend.getOrdering())
                .createdDate(friend.getCreatedDate())
                .build();
    }
}
