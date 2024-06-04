package lostark.todo.domain.friends;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;

import java.util.Optional;

import static lostark.todo.domain.character.QCharacter.character;
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
        return Optional.ofNullable(factory.select(friends)
                .from(friends)
                .leftJoin(member).on(friends.member.eq(member))
                .where(member.username.eq(friendUsername).and(eqUsername(username)))
                .fetchOne());
    }

    @Override
    public long deleteByMember(Member member) {
        return factory.delete(friends)
                .where(friends.member.eq(member).or(friends.fromMember.eq(member.getId())))
                .execute();
    }

    private BooleanExpression eqUsername(String username) {
        return friends.fromMember.eq(JPAExpressions.select(member.id).from(member).where(member.username.eq(username)));
    }

    private BooleanExpression eqFriendUsername(String friendUsername) {
        return friends.member.username.eq(friendUsername);
    }

    private BooleanExpression eqCharacterId(long characterId) {
        return character.id.eq(characterId);
    }
}
