package lostark.todo.domain.character;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.controller.adminDto.QDashboardResponse;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.member.Member;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.content.QDayContent.dayContent;
import static lostark.todo.domain.member.QMember.member;
import static lostark.todo.domain.todo.QTodo.todo;
import static lostark.todo.domain.todoV2.QTodoV2.todoV2;

@RequiredArgsConstructor
public class CharacterRepositoryImpl implements CharacterCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Optional<Character> getByIdAndUsername(long characterId, String username) {
        return Optional.ofNullable(factory.selectFrom(character)
                .leftJoin(character.member, member).fetchJoin()
                .leftJoin(character.dayTodo.chaos, dayContent).fetchJoin()
                .leftJoin(character.dayTodo.guardian, dayContent).fetchJoin()
                .leftJoin(character.todoV2List, todoV2).fetchJoin()
                .where(
                        eqMember(username),
                        eqCharacterId(characterId)
                )
                .fetchOne());
    }

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

    @Override
    public long updateDayContentGauge() {
        return factory.update(character)
                .set(character.dayTodo.eponaGauge,
                        new CaseBuilder()
                                .when(character.dayTodo.eponaGauge.add(
                                        character.dayTodo.eponaCheck2.multiply(10).negate().add(30)).gt(100))
                                .then(100)
                                .otherwise(character.dayTodo.eponaGauge.add(
                                        character.dayTodo.eponaCheck2.multiply(10).negate().add(30))))
                .set(character.dayTodo.chaosGauge,
                        new CaseBuilder()
                                .when(character.dayTodo.chaosGauge.add(
                                        character.dayTodo.chaosCheck.multiply(10).negate().add(20)).gt(200))
                                .then(200)
                                .otherwise(character.dayTodo.chaosGauge.add(
                                        character.dayTodo.chaosCheck.multiply(10).negate().add(20))))
                .set(character.dayTodo.guardianGauge,
                        new CaseBuilder()
                                .when(character.dayTodo.guardianGauge.add(
                                        character.dayTodo.guardianCheck.multiply(10).negate().add(10)).gt(100))
                                .then(100)
                                .otherwise(character.dayTodo.guardianGauge.add(
                                        character.dayTodo.guardianCheck.multiply(10).negate().add(10))))
                .execute();
    }

    @Override
    public long saveBeforeGauge() {
        return factory.update(character)
                .set(character.dayTodo.beforeEponaGauge, character.dayTodo.eponaGauge)
                .set(character.dayTodo.beforeChaosGauge, character.dayTodo.chaosGauge)
                .set(character.dayTodo.beforeGuardianGauge, character.dayTodo.guardianGauge)
                .where(character.dayTodo.beforeEponaGauge.ne(character.dayTodo.eponaGauge)
                        .or(character.dayTodo.beforeChaosGauge.ne(character.dayTodo.chaosGauge))
                        .or(character.dayTodo.beforeGuardianGauge.ne(character.dayTodo.guardianGauge)))
                .execute();
    }

    @Override
    public long updateDayContentCheck() {
        return factory.update(character)
                .set(character.dayTodo.eponaCheck2, 0)
                .set(character.dayTodo.chaosCheck, 0)
                .set(character.dayTodo.guardianCheck, 0)
                .execute();
    }

    @Override
    public void updateDayContentPriceChaos(DayContent dayContent, Double price) {
        factory.update(character)
                .set(character.dayTodo.chaosGold,
                        new CaseBuilder()
                                .when(character.dayTodo.chaosGauge.goe(40)).then(price * 4.0)
                                .when(character.dayTodo.chaosGauge.goe(20)).then(price * 3.0)
                                .otherwise(price * 2.0))
                .where(character.dayTodo.chaos.eq(dayContent))
                .execute();
    }

    @Override
    public void updateDayContentPriceGuardian(DayContent dayContent, Double price) {
        factory.update(character)
                .set(character.dayTodo.guardianGold,
                        new CaseBuilder()
                                .when(character.dayTodo.guardianGauge.goe(20)).then(price * 2.0)
                                .otherwise(price))
                .where(character.dayTodo.guardian.eq(dayContent))
                .execute();
    }

    private BooleanExpression eqMember(Member member) {
        return character.member.eq(member);
    }

    private BooleanExpression eqMember(String username) {
        return character.member.username.eq(username);
    }

    private BooleanExpression eqCharacterId(long characterId) {
        return character.id.eq(characterId);
    }

}
