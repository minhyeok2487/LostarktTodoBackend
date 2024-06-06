package lostark.todo.domain.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.content.QDayContent.dayContent;
import static lostark.todo.domain.member.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Member findMemberAndCharacters(String username) {
        return factory.select(member)
                .from(member)
                .leftJoin(member.characters, character).fetchJoin()
                .leftJoin(character.dayTodo.chaos, dayContent).fetchJoin()
                .leftJoin(character.dayTodo.guardian, dayContent).fetchJoin()
                .where(eqUsername(username))
                .fetchOne();
    }

    @Override
    public Member get(String username) {
        return factory.selectFrom(member).where(eqUsername(username)).fetchOne();
    }

    private BooleanExpression eqUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.eq(username);
        }
        return null;
    }
}
