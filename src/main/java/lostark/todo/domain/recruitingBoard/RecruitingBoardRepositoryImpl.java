package lostark.todo.domain.recruitingBoard;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.recruitingBoard.SearchRecruitingBoardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.content.QDayContent.dayContent;
import static lostark.todo.domain.member.QMember.member;
import static lostark.todo.domain.recruitingBoard.QRecruitingBoard.recruitingBoard;

@RequiredArgsConstructor
public class RecruitingBoardRepositoryImpl implements RecruitingBoardCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Page<RecruitingBoard> search(SearchRecruitingBoardRequest request, PageRequest pageRequest) {
        List<RecruitingBoard> fetch = factory.select(recruitingBoard)
                .from(recruitingBoard)
                .leftJoin(recruitingBoard.member, member).fetchJoin()
                .where(
                        eqRecruitingCategory(request.getRecruitingCategory())
                )
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        long total = factory.selectFrom(recruitingBoard).where(
                eqRecruitingCategory(request.getRecruitingCategory())
        ).fetchCount();

        return new PageImpl<>(fetch, pageRequest, total);
    }

    @Override
    public Optional<RecruitingBoard> get(long recruitingBoardId) {
        RecruitingBoard result =
                factory.select(recruitingBoard)
                        .from(recruitingBoard)
                        .leftJoin(recruitingBoard.member, member).fetchJoin()
                        .leftJoin(member.characters, character).fetchJoin()
                        .leftJoin(character.dayTodo.chaos, dayContent).fetchJoin()
                        .leftJoin(character.dayTodo.guardian, dayContent).fetchJoin()
                        .where(
                                recruitingBoard.id.eq(recruitingBoardId)
                        ).fetchOne();
        return Optional.ofNullable(result);
    }

    private BooleanExpression eqRecruitingCategory(RecruitingCategoryEnum recruitingCategory) {
        if (recruitingCategory == null) {
            return null;
        }
        return recruitingBoard.recruitingCategory.eq(recruitingCategory);
    }
}
