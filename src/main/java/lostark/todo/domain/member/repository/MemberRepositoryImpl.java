package lostark.todo.domain.member.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.DashboardResponse;
import lostark.todo.domain.admin.dto.DashboardSummaryResponse;
import lostark.todo.domain.admin.dto.QDashboardResponse;
import lostark.todo.domain.admin.dto.QSearchAdminMemberResponse;
import lostark.todo.domain.admin.dto.SearchAdminMemberRequest;
import lostark.todo.domain.admin.dto.SearchAdminMemberResponse;
import lostark.todo.domain.admin.enums.MemberSortBy;
import org.springframework.data.domain.Sort;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.entity.QMember;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.character.entity.QCharacter.character;
import static lostark.todo.domain.content.entity.QDayContent.dayContent;
import static lostark.todo.global.exhandler.ErrorMessageConstants.MEMER_NOT_FOUND;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory factory;
    private final EntityManager entityManager;

    @Override
    public Member get(String username) {
        return findMemberBy(member -> member.username.eq(username));
    }

    @Override
    public Member get(Long id) {
        return findMemberBy(member -> member.id.eq(id));
    }

    private Member findMemberBy(Function<QMember, BooleanExpression> whereCondition) {
        return Optional.ofNullable(
                factory.select(member)
                        .from(member)
                        .leftJoin(member.characters, character).fetchJoin()
                        .leftJoin(character.dayTodo.chaos, dayContent).fetchJoin()
                        .leftJoin(character.dayTodo.guardian, dayContent).fetchJoin()
                        .where(whereCondition.apply(member))
                        .fetchOne()
        ).orElseThrow(() -> new ConditionNotMetException(MEMER_NOT_FOUND));
    }

    @Override
    public List<DashboardResponse> searchMemberDashBoard(int limit) {
        StringTemplate dateTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                member.createdDate,
                ConstantImpl.create("%Y-%m-%d")
        );

        return factory
                .select(new QDashboardResponse(
                        dateTemplate,
                        member.id.count().intValue()))
                .from(member)
                .groupBy(dateTemplate)
                .orderBy(dateTemplate.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public PageImpl<SearchAdminMemberResponse> searchAdminMember(SearchAdminMemberRequest request, PageRequest pageRequest) {
        List<SearchAdminMemberResponse> fetch = factory.select(new QSearchAdminMemberResponse(
                        member.id, member.username, member.createdDate, member.authProvider,
                        member.mainCharacter, member.apiKey
                ))
                .from(member)
                .where(
                        containsUsername(request.getUsername()),
                        eqAuthProvider(request.getAuthProvider()),
                        eqMainCharacter(request.getMainCharacter())
                )
                .orderBy(getOrderSpecifier(request.getSortBy(), request.getSortDirection()))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        long total = factory.selectFrom(member).where(
                containsUsername(request.getUsername()),
                eqAuthProvider(request.getAuthProvider()),
                eqMainCharacter(request.getMainCharacter())
        ).fetchCount();

        return new PageImpl<>(fetch, pageRequest, total);
    }

    private OrderSpecifier<?> getOrderSpecifier(MemberSortBy sortBy, Sort.Direction sortDirection) {
        boolean isAsc = sortDirection == Sort.Direction.ASC;

        return switch (sortBy) {
            case MEMBER_ID -> isAsc ? member.id.asc() : member.id.desc();
            case USERNAME -> isAsc ? member.username.asc() : member.username.desc();
            case AUTH_PROVIDER -> isAsc ? member.authProvider.asc() : member.authProvider.desc();
            case MAIN_CHARACTER -> isAsc ? member.mainCharacter.asc() : member.mainCharacter.desc();
            default -> isAsc ? member.createdDate.asc() : member.createdDate.desc();
        };
    }

    private BooleanExpression containsUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.contains(username);
        }
        return null;
    }

    private BooleanExpression eqAuthProvider(String authProvider) {
        if (StringUtils.hasText(authProvider)) {
            return member.authProvider.eq(authProvider);
        }
        return null;
    }

    private BooleanExpression eqMainCharacter(String mainCharacter) {
        if (StringUtils.hasText(mainCharacter)) {
            return member.mainCharacter.eq(mainCharacter);
        }
        return null;
    }

    @Override
    public long countActiveMembers() {
        Long count = factory
                .select(member.id.countDistinct())
                .from(member)
                .innerJoin(member.characters, character)
                .where(character.isDeleted.eq(false))
                .fetchOne();
        return count != null ? count : 0;
    }

    @Override
    public DashboardSummaryResponse getDashboardSummaryOptimized() {
        String sql = """
            SELECT
                (SELECT COUNT(*) FROM member) as total_members,
                (SELECT COUNT(*) FROM characters) as total_characters,
                (SELECT COUNT(DISTINCT m.member_id) FROM member m
                    INNER JOIN characters c ON m.member_id = c.member_id
                    WHERE c.is_deleted = false) as active_members,
                (SELECT COUNT(*) FROM member WHERE DATE(created_date) = CURDATE()) as today_new_members,
                (SELECT COUNT(*) FROM characters WHERE DATE(created_date) = CURDATE()) as today_new_characters
            """;

        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();

        return DashboardSummaryResponse.builder()
                .totalMembers(((Number) result[0]).longValue())
                .totalCharacters(((Number) result[1]).longValue())
                .activeMembers(((Number) result[2]).longValue())
                .todayNewMembers(((Number) result[3]).longValue())
                .todayNewCharacters(((Number) result[4]).longValue())
                .build();
    }
}
