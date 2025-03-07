package lostark.todo.domain.friend.repository;

import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.friend.entity.Friends;
import lostark.todo.domain.friend.enums.FriendStatus;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FriendsCustomRepository {
    Character findFriendCharacter(String friendUsername, long characterId);

    Optional<Friends> findByFriendUsername(String friendUsername, String username);

    long deleteByMember(@Param("member") Member member);

    List<Friends> getFriendList(long memberId);

    long deleteByMemberFriend(long id, long fromMember);

    void updateSort(Map<Long, Integer> idOrderingMap);

    FriendStatus isFriend(long toMemberId, long fromMemberId);
}
