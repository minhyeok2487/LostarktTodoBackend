package lostark.todo.domainV2.board.recrutingBoard.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.recrutingBoard.dto.SearchRecruitingBoardRequest;
import lostark.todo.domainV2.board.recrutingBoard.enums.RecruitingCategoryEnum;
import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.character.QCharacter.character;
import static lostark.todo.domain.content.QDayContent.dayContent;
import static lostark.todo.domain.member.QMember.member;
import static lostark.todo.domainV2.board.recrutingBoard.entity.QRecruitingBoard.recruitingBoard;

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
