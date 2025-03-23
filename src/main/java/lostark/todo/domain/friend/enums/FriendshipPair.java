package lostark.todo.domain.friend.enums;

import lostark.todo.domain.friend.entity.Friends;

public record FriendshipPair(Friends toFriend, Friends fromFriend) {
}
