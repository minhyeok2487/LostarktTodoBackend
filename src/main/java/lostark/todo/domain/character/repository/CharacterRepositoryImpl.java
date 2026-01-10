package lostark.todo.domain.character.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lostark.todo.domain.admin.enums.CharacterSortBy;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.AdminCharacterResponse;
import lostark.todo.domain.admin.dto.AdminCharacterSearchRequest;
import lostark.todo.domain.admin.dto.DashboardResponse;
import lostark.todo.domain.admin.dto.QDashboardResponse;
import lostark.todo.domain.character.dto.DeletedCharacterResponse;
import lostark.todo.domain.character.dto.QDeletedCharacterResponse;
import lostark.todo.domain.content.entity.DayContent;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.character.entity.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.character.entity.QRaidBusGold.*;
import static lostark.todo.domain.content.entity.QDayContent.dayContent;
import static lostark.todo.domain.content.entity.QWeekContent.weekContent;
import static lostark.todo.domain.character.entity.QTodoV2.todoV2;
import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.character.entity.QCharacter.character;

@RequiredArgsConstructor
public class CharacterRepositoryImpl implements CharacterCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Optional<Character> getByIdAndUsername(long characterId, String username) {
        return Optional.ofNullable(factory.selectDistinct(character)
                .from(character)
                .leftJoin(character.member, member).fetchJoin()
                .leftJoin(character.dayTodo.chaos, dayContent).fetchJoin()
                .leftJoin(character.dayTodo.guardian, dayContent).fetchJoin()
                .leftJoin(character.todoV2List, todoV2).fetchJoin()
                .leftJoin(todoV2.weekContent, weekContent).fetchJoin()
                .where(
                        eqMember(username),
                        eqCharacterId(characterId)
                )
                .fetchOne());
    }

    @Override
    public List<Character> getCharacterList(String username) {
        return factory.selectDistinct(character)
                .from(character)
                .leftJoin(character.member, member).fetchJoin()
                .leftJoin(character.dayTodo.chaos, dayContent).fetchJoin()
                .leftJoin(character.dayTodo.guardian, dayContent).fetchJoin()
                .leftJoin(character.todoV2List, todoV2).fetchJoin()
                .leftJoin(todoV2.weekContent, weekContent).fetchJoin()
                .where(
                        eqMember(username),
                        isDeleted(false)
                )
                .fetch();
    }

    @Override
    public List<Character> getCharacter(String characterName) {
        return factory.selectFrom(character)
                .leftJoin(character.member, member).fetchJoin()
                .where(character.characterName.eq(characterName)).fetch();
    }

    @Override
    public List<DeletedCharacterResponse> getDeletedCharacter(String username) {
        return factory.select(new QDeletedCharacterResponse(
                        character.id, character.characterClassName,
                        character.characterImage, character.characterName,
                        character.itemLevel, character.serverName
                ))
                .from(character)
                .leftJoin(character.member, member)
                .where(
                        eqMember(username),
                        isDeleted(true)
                )
                .fetch();
    }

    @Override
    public long deleteByMember(Member member) {
        factory.delete(todoV2)
                .where(todoV2.character.in(member.getCharacters()))
                .execute();

        factory.delete(raidBusGold)
                .where(raidBusGold.character.in(member.getCharacters()))
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

//    @Override
//    public void updateDayContentPriceChaos(DayContent dayContent, Double price) {
//        factory.update(character)
//                .set(character.dayTodo.chaosGold,
//                        new CaseBuilder()
//                                .when(character.dayTodo.chaosGauge.goe(40)).then(price * 2.0)
//                                .otherwise(price))
//                .where(character.dayTodo.chaos.eq(dayContent))
//                .execute();
//    }

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

    private BooleanExpression isDeleted(boolean isDeleted) {
        return character.isDeleted.eq(isDeleted);
    }

    @Override
    public Page<AdminCharacterResponse> searchAdminCharacter(AdminCharacterSearchRequest request, Pageable pageable) {
        List<Character> content = factory.selectFrom(character)
                .leftJoin(character.member, member).fetchJoin()
                .where(
                        eqMemberId(request.getMemberId()),
                        eqServerName(request.getServerName()),
                        containsCharacterName(request.getCharacterName()),
                        eqCharacterClassName(request.getCharacterClassName()),
                        goeItemLevel(request.getMinItemLevel()),
                        loeItemLevel(request.getMaxItemLevel()),
                        eqIsDeleted(request.getIsDeleted())
                )
                .orderBy(getCharacterOrderSpecifier(request.getSortBy(), request.getSortDirection()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = factory.select(character.count())
                .from(character)
                .where(
                        eqMemberId(request.getMemberId()),
                        eqServerName(request.getServerName()),
                        containsCharacterName(request.getCharacterName()),
                        eqCharacterClassName(request.getCharacterClassName()),
                        goeItemLevel(request.getMinItemLevel()),
                        loeItemLevel(request.getMaxItemLevel()),
                        eqIsDeleted(request.getIsDeleted())
                )
                .fetchOne();

        List<AdminCharacterResponse> responses = content.stream()
                .map(AdminCharacterResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total != null ? total : 0);
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return memberId != null ? character.member.id.eq(memberId) : null;
    }

    private BooleanExpression eqServerName(String serverName) {
        return serverName != null ? character.serverName.eq(serverName) : null;
    }

    private BooleanExpression containsCharacterName(String characterName) {
        return characterName != null ? character.characterName.contains(characterName) : null;
    }

    private BooleanExpression eqCharacterClassName(String className) {
        return className != null ? character.characterClassName.eq(className) : null;
    }

    private BooleanExpression goeItemLevel(Double minLevel) {
        return minLevel != null ? character.itemLevel.goe(minLevel) : null;
    }

    private BooleanExpression loeItemLevel(Double maxLevel) {
        return maxLevel != null ? character.itemLevel.loe(maxLevel) : null;
    }

    private BooleanExpression eqIsDeleted(Boolean isDeleted) {
        return isDeleted != null ? character.isDeleted.eq(isDeleted) : null;
    }

    private OrderSpecifier<?> getCharacterOrderSpecifier(CharacterSortBy sortBy, Sort.Direction sortDirection) {
        boolean isAsc = sortDirection == Sort.Direction.ASC;

        return switch (sortBy) {
            case MEMBER_ID -> isAsc
                    ? character.member.id.asc().nullsLast()
                    : character.member.id.desc().nullsLast();
            case SERVER_NAME -> isAsc ? character.serverName.asc() : character.serverName.desc();
            case CHARACTER_NAME -> isAsc ? character.characterName.asc() : character.characterName.desc();
            case CHARACTER_CLASS_NAME -> isAsc ? character.characterClassName.asc() : character.characterClassName.desc();
            case ITEM_LEVEL -> isAsc ? character.itemLevel.asc() : character.itemLevel.desc();
            case CREATED_DATE -> isAsc ? character.createdDate.asc() : character.createdDate.desc();
            default -> isAsc ? character.id.asc() : character.id.desc();
        };
    }
}
