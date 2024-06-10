package lostark.todo.domain.friends;

import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendsCustomRepository {
    Character findFriendCharacter(String friendUsername, long characterId);

    Optional<Friends> findByFriendUsername(String friendUsername, String username);

    long deleteByMember(@Param("member") Member member);

    List<Friends> getFriendList(long memberId);

    long deleteByMemberFriend(long id, long fromMember);
}
