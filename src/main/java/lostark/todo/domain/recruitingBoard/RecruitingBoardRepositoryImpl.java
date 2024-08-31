package lostark.todo.domain.recruitingBoard;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.recruitingBoard.SearchRecruitingBoardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static lostark.todo.domain.member.QMember.member;
import static lostark.todo.domain.recruitingBoard.QRecruitingBoard.recruitingBoard;

@RequiredArgsConstructor
public class RecruitingBoardRepositoryImpl implements RecruitingBoardCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Page<RecruitingBoard> search(SearchRecruitingBoardRequest request, PageRequest pageRequest) {
        List<RecruitingBoard> fetch = factory.select(recruitingBoard)
                .from(recruitingBoard)
                .leftJoin(recruitingBoard.member, member)
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

    private BooleanExpression eqRecruitingCategory(RecruitingCategoryEnum recruitingCategory) {
        if (recruitingCategory == null) {
            return null;
        }
        return recruitingBoard.recruitingCategory.eq(recruitingCategory);
    }
}
