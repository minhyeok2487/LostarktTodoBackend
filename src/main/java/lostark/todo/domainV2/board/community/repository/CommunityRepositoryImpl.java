package lostark.todo.domainV2.board.community.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.dto.CommunityResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.entity.CommunityCategory;
import lostark.todo.domainV2.board.community.entity.QCommunity;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static lostark.todo.domainV2.board.community.entity.QCommunity.community;
import static lostark.todo.domainV2.board.community.entity.QCommunityLike.communityLike;

@RequiredArgsConstructor
public class CommunityRepositoryImpl implements CommunityCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public CursorResponse<CommunityResponse> search(long memberId, CommunitySearchParams params, PageRequest pageRequest) {
        QCommunity communitySub = new QCommunity("communitySub");
        List<CommunityResponse> fetch = factory.select(
                        Projections.fields(CommunityResponse.class,
                                community.id.as("communityId"),
                                community.createdDate.as("createdDate"),
                                community.name.as("name"),
                                community.body.as("body"),
                                community.category.as("category"),
                                new CaseBuilder()
                                        .when(community.memberId.eq(memberId))
                                        .then(true)
                                        .otherwise(false).as("myPost"),
                                new CaseBuilder()
                                        .when(JPAExpressions
                                                .selectOne()
                                                .from(communityLike)
                                                .where(communityLike.communityId.eq(community.id)
                                                        .and(communityLike.memberId.eq(memberId)))
                                                .exists())
                                        .then(true)
                                        .otherwise(false).as("myLike"),
                                Expressions.as(JPAExpressions
                                        .select(communitySub.count())
                                        .from(communitySub)
                                        .where(communitySub.rootParentId.eq(community.id)), "commentCount"),
                                community.likeCount.as("likeCount")
                        ))
                .from(community)
                .where(
                        ltCommunityId(params.getCommunityId()),
                        eqCategory(params.getCategory()),
                        eqRoot(0)
                )
                .orderBy(community.id.desc())
                .limit(pageRequest.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if (fetch.size() > pageRequest.getPageSize()) {
            fetch.remove(pageRequest.getPageSize());
            hasNext = true;
        }

        return new CursorResponse<>(fetch, hasNext);
    }

    private BooleanExpression ltCommunityId(Long communityId) {
        if (communityId != null) {
            return community.id.lt(communityId);
        }
        return null;
    }

    private BooleanExpression eqCategory(CommunityCategory category) {
        if (category != null) {
            return community.category.lt(category);
        }
        return null;
    }

    private BooleanExpression eqRoot(long rootParentId) {
        return community.rootParentId.eq(rootParentId);
    }
}
