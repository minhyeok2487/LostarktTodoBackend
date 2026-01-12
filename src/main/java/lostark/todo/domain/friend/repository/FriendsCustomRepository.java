package lostark.todo.domain.friend.repository;

import lostark.todo.domain.friend.enums.FriendshipPair;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.friend.enums.FriendStatus;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;

public interface FriendsCustomRepository {
    Optional<Friends> findByFriendUsername(String friendUsername, String username);

    long deleteByMember(@Param("member") Member member);

    long deleteByMemberFriend(long id, long fromMember);

    void updateSort(Map<Long, Integer> idOrderingMap);

    FriendStatus isFriend(long toMemberId, long fromMemberId);

    Map<Long, FriendshipPair> findFriendshipPairs(long memberId);

    void deleteByIdSafe(Long id);
}
