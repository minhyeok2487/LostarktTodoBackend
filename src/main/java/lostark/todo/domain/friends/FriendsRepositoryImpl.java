package lostark.todo.domain.friends;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.member.QMember;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.content.QDayContent.dayContent;
import static lostark.todo.domain.friends.QFriends.friends;
import static lostark.todo.domain.member.QMember.member;

@RequiredArgsConstructor
public class FriendsRepositoryImpl implements FriendsCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Character findFriendCharacter(String friendUsername, long characterId) {
        return factory.select(character)
                .from(friends)
                .leftJoin(member).on(friends.member.eq(member))
                .leftJoin(character).on(character.member.eq(member))
                .where(
                        eqFriendUsername(friendUsername),
                        eqCharacterId(characterId)
                ).fetchOne();
    }

    @Override
    public Optional<Friends> findByFriendUsername(String friendUsername, String username) {
        QMember friendMember = new QMember("friendMember");
        return Optional.ofNullable(
                factory.select(friends)
                        .from(friends)
                        .join(friends.member, member).fetchJoin()
                        .join(member.characters, character).fetchJoin()
                        .join(character.dayTodo.chaos, dayContent).fetchJoin()
                        .join(character.dayTodo.guardian, dayContent).fetchJoin()
                        .join(friendMember).on(friends.fromMember.eq(friendMember.id))
                        .where(member.username.eq(friendUsername)
                                .and(friendMember.username.eq(username)))
                        .fetchOne());
    }

    @Override
    public long deleteByMember(Member member) {
        return factory.delete(friends)
                .where(friends.member.eq(member).or(friends.fromMember.eq(member.getId())))
                .execute();
    }

    @Override
    public List<Friends> getFriendList(long memberId) {
        return factory.select(friends)
                .from(friends)
                .leftJoin(friends.member, member).fetchJoin()
                .leftJoin(member.characters, character).fetchJoin()
                .leftJoin(character.dayTodo.chaos, dayContent).fetchJoin()
                .leftJoin(character.dayTodo.guardian, dayContent).fetchJoin()
                .where(friends.member.id.eq(memberId).or(friends.fromMember.eq(memberId)))
                .fetch();
    }

    @Override
    public long deleteByMemberFriend(long id, long fromMember) {
        return factory.delete(friends)
                .where(friends.member.id.eq(id).and(friends.fromMember.eq(fromMember)))
                .execute();
    }


    private BooleanExpression eqFriendUsername(String friendUsername) {
        return friends.member.username.eq(friendUsername);
    }

    private BooleanExpression eqCharacterId(long characterId) {
        return character.id.eq(characterId);
    }
}
