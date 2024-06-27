package lostark.todo.domain.member;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.controller.adminDto.QDashboardResponse;
import lostark.todo.controller.dtoV2.admin.QSearchAdminMemberResponse;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberResponse;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.util.List;

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
                        member.id, member.username, member.createdDate, member.authProvider, member.mainCharacter
                ))
                .from(member)
                .where(
                        containsUsername(request.getUsername()),
                        eqAuthProvider(request.getAuthProvider()),
                        eqMainCharacter(request.getMainCharacter())
                )
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

    private BooleanExpression eqUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.eq(username);
        }
        return null;
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
}
