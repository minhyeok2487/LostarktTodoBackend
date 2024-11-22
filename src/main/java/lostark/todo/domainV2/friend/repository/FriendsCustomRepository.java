package lostark.todo.domainV2.friend.repository;

import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.member.entity.Member;
import lostark.todo.domainV2.friend.entity.Friends;
import lostark.todo.domainV2.friend.enums.FriendStatus;
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
