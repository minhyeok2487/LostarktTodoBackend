package lostark.todo.domainV2.board.community.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.dto.CommunityCommentResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.entity.Community;
import lostark.todo.domainV2.board.community.entity.CommunityCategory;
import lostark.todo.domainV2.board.community.entity.QCommunity;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.member.QMember.member;
import static lostark.todo.domainV2.board.community.entity.QCommunity.community;
import static lostark.todo.domainV2.board.community.entity.QCommunityLike.communityLike;

@RequiredArgsConstructor
public class CommunityRepositoryImpl implements CommunityCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public CursorResponse<CommunitySearchResponse> search(long memberId, CommunitySearchParams params, PageRequest pageRequest) {
        QCommunity communitySub = new QCommunity("communitySub");
        List<CommunitySearchResponse> fetch = factory.select(
                        Projections.fields(CommunitySearchResponse.class,
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
                        eqRoot(0),
                        isDeleted(false)
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

    @Override
    public Optional<Community> get(String username, long communityId) {
        return Optional.ofNullable(factory.selectFrom(community)
                .leftJoin(member).on(community.memberId.eq(member.id))
                .where(
                        eqUsername(username),
                        eqCommunityId(communityId),
                        isDeleted(false)
                ).fetchOne());
    }

    @Override
    public Optional<CommunitySearchResponse> getResponse(long memberId, long communityId) {
        QCommunity communitySub = new QCommunity("communitySub");
        CommunitySearchResponse fetch = factory.select(
                        Projections.fields(CommunitySearchResponse.class,
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
                        eqCommunityId(communityId),
                        isDeleted(false)
                )
                .orderBy(community.id.desc())
                .fetchOne();
        return Optional.ofNullable(fetch);
    }

    @Override
    public List<CommunityCommentResponse> getComments(long memberId, Long rootParentId) {
        return factory.select(
                        Projections.fields(CommunityCommentResponse.class,
                                community.id.as("commentId"),
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
                                community.likeCount.as("likeCount"),
                                community.rootParentId.as("rootParentId"),
                                community.commentParentId.as("commentParentId")
                        ))
                .from(community)
                .where(
                        eqRoot(rootParentId),
                        isDeleted(false)
                )
                .orderBy(community.id.asc())
                .fetch();
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

    private BooleanExpression isDeleted(boolean deleted) {
        return community.deleted.eq(deleted);
    }

    private BooleanExpression eqUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.eq(username);
        }
        return null;
    }

    private BooleanExpression eqCommunityId(Long communityId) {
        if (communityId != null) {
            return community.id.eq(communityId);
        }
        return null;
    }
}
