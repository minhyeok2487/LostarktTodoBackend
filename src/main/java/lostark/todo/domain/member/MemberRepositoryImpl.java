package lostark.todo.domain.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.member.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Member findMemberAndMainCharacter(String username) {
        return factory.select(member)
                .from(member)
                .leftJoin(member.characters, character).fetchJoin()
                .where(eqUsername(username))
                .fetchOne();
    }

    private BooleanExpression eqUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.eq(username);
        }
        return null;
    }
}
