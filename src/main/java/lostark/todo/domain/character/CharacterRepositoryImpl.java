package lostark.todo.domain.character;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.controller.adminDto.QDashboardResponse;
import lostark.todo.domain.member.Member;

import java.util.List;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.todo.QTodo.todo;
import static lostark.todo.domain.todoV2.QTodoV2.todoV2;

@RequiredArgsConstructor
public class CharacterRepositoryImpl implements CharacterCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public long deleteByMember(Member member) {
        factory.delete(todo)
                .where(todo.character.in(member.getCharacters()))
                .execute();

        factory.delete(todoV2)
                .where(todoV2.character.in(member.getCharacters()))
                .execute();

        return factory.delete(character)
                .where(eqMember(member))
                .execute();
    }

    @Override
    public List<DashboardResponse> searchCharactersDashBoard(int limit) {
        StringTemplate dateTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                character.createdDate,
                ConstantImpl.create("%Y-%m-%d")
        );

        return factory
                .select(new QDashboardResponse(
                        dateTemplate,
                        character.id.count().intValue()))
                .from(character)
                .groupBy(dateTemplate)
                .orderBy(dateTemplate.desc())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression eqMember(Member member) {
        return character.member.eq(member);
    }
}
