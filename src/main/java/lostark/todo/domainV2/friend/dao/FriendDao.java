package lostark.todo.domainV2.friend.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.friend.repository.FriendsRepository;
import lostark.todo.domainV2.friend.enums.FriendStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Repository
public class FriendDao {

    private final FriendsRepository friendsRepository;

    @Transactional
    public FriendStatus isFriend(long toMemberId, long fromMemberId) {
        return friendsRepository.isFriend(toMemberId, fromMemberId);
    }
}
