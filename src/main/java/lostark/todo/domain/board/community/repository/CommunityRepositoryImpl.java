package lostark.todo.domain.board.community.repository;

import com.amazonaws.services.kms.model.NotFoundException;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.board.community.dto.CommunityCommentResponse;
import lostark.todo.domain.board.community.dto.CommunitySearchResponse;
import lostark.todo.domain.board.community.dto.CommunitySearchParams;
import lostark.todo.domain.board.community.entity.Community;
import lostark.todo.domain.board.community.entity.CommunityCategory;
import lostark.todo.domain.board.community.entity.QCommunity;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.board.community.entity.QCommunity.community;
import static lostark.todo.domain.board.community.entity.QCommunityImages.communityImages;
import static lostark.todo.domain.board.community.entity.QCommunityLike.communityLike;

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
                                community.characterImage.as("characterImage"),
                                community.characterClassName.as("characterClassName"),
                                community.name.as("name"),
                                community.memberId.id.as("memberId"),
                                community.body.as("body"),
                                community.category.as("category"),
                                new CaseBuilder()
                                        .when(community.memberId.id.eq(memberId))
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
                                        .where(communitySub.rootParentId.eq(community.id)
                                                .and(communitySub.deleted.eq(false))), "commentCount"),
                                Expressions.as(JPAExpressions
                                        .select(communityLike.count())
                                        .from(communityLike)
                                        .where(communityLike.communityId.eq(community.id)), "likeCount")
                        ))
                .from(community)
                .where(
                        ltCommunityId(params.getCommunityId()),
                        eqCategory(params.getCategory()),
                        eqRootParentId(0),
                        eqCommentParentId(0),
                        isDeleted(false)
                )
                .orderBy(community.createdDate.desc())
                .limit(pageRequest.getPageSize() + 1)
                .fetch();

        // 각 커뮤니티에 대한 이미지 리스트를 별도로 조회
        fetch.forEach(response -> {
            List<String> images = factory
                    .select(communityImages.url)
                    .from(communityImages)
                    .where(communityImages.communityId.eq(response.getCommunityId()))
                    .orderBy(communityImages.ordering.asc())
                    .fetch();
            response.setImageList(images);
        });

        boolean hasNext = false;

        if (fetch.size() > pageRequest.getPageSize()) {
            fetch.remove(pageRequest.getPageSize());
            hasNext = true;
        }

        return new CursorResponse<>(fetch, hasNext);
    }

    @Override
    public Community get(String username, long communityId) {
        return Optional.ofNullable(factory.selectFrom(community)
                .leftJoin(member).on(community.memberId.id.eq(member.id))
                .where(
                        eqUsername(username),
                        eqCommunityId(communityId),
                        isDeleted(false)
                ).fetchOne()).orElseThrow(()-> new NullPointerException("존재하지 않는 게시글 입니다."));
    }

    @Override
    public CommunitySearchResponse getResponse(long memberId, long communityId) {
        QCommunity communitySub = new QCommunity("communitySub");
        CommunitySearchResponse fetch = factory.select(
                        Projections.fields(CommunitySearchResponse.class,
                                community.id.as("communityId"),
                                community.createdDate.as("createdDate"),
                                community.characterImage.as("characterImage"),
                                community.characterClassName.as("characterClassName"),
                                community.name.as("name"),
                                community.memberId.id.as("memberId"),
                                community.body.as("body"),
                                community.category.as("category"),
                                new CaseBuilder()
                                        .when(community.memberId.id.eq(memberId))
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
                                        .where(communitySub.rootParentId.eq(community.id)
                                                .and(communitySub.deleted.eq(false))), "commentCount"),
                                Expressions.as(JPAExpressions
                                        .select(communityLike.count())
                                        .from(communityLike)
                                        .where(communityLike.communityId.eq(community.id)), "likeCount")
                        ))
                .from(community)
                .where(
                        eqCommunityId(communityId),
                        isDeleted(false)
                )
                .orderBy(community.createdDate.desc())
                .fetchOne();

        if (fetch != null) {
            List<String> images = factory
                    .select(communityImages.url)
                    .from(communityImages)
                    .where(communityImages.communityId.eq(communityId))
                    .orderBy(communityImages.ordering.asc())
                    .fetch();
            fetch.setImageList(images);
            return fetch;
        } else {
            throw new NullPointerException("존재하지 않는 게시글 입니다.");
        }
    }

    @Override
    public List<CommunityCommentResponse> getComments(long memberId, Long rootParentId) {
        return factory.select(
                        Projections.fields(CommunityCommentResponse.class,
                                community.id.as("commentId"),
                                community.createdDate.as("createdDate"),
                                community.characterImage.as("characterImage"),
                                community.characterClassName.as("characterClassName"),
                                community.name.as("name"),
                                community.memberId.id.as("memberId"),
                                community.body.as("body"),
                                community.category.as("category"),
                                new CaseBuilder()
                                        .when(community.memberId.id.eq(memberId))
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
                                        .select(communityLike.count())
                                        .from(communityLike)
                                        .where(communityLike.communityId.eq(community.id)), "likeCount"),
                                community.rootParentId.as("rootParentId"),
                                community.commentParentId.as("commentParentId")
                        ))
                .from(community)
                .where(
                        eqRootParentId(rootParentId),
                        isDeleted(false)
                )
                .orderBy(community.id.asc())
                .fetch();
    }

    @Override
    public Community get(long communityId) {
        return Optional.ofNullable(
                factory.selectFrom(community)
                        .leftJoin(member).on(community.memberId.eq(member))
                        .where(
                                eqCommunityId(communityId),
                                isDeleted(false)
                        ).fetchOne()
        ).orElseThrow(() -> new NotFoundException("데이터를 찾을 수 없습니다."));
    }

    @Override
    public List<Community> searchBoards() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeeksAgo = now.minusWeeks(1);

        return factory.selectFrom(community)
                .where(
                        eqRootParentId(0L),
                        eqCategory(CommunityCategory.BOARDS),
                        betweenDate(oneWeeksAgo, now)
                )
                .orderBy(community.createdDate.desc())
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
            return community.category.eq(category);
        }
        return null;
    }

    private BooleanExpression eqRootParentId(long rootParentId) {
        return community.rootParentId.eq(rootParentId);
    }

    private BooleanExpression eqCommentParentId(long commentParentId) {
        return community.commentParentId.eq(commentParentId);
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

    private BooleanExpression betweenDate(LocalDateTime beforeDate, LocalDateTime afterDate) {
        return community.createdDate.between(beforeDate, afterDate);
    }
}
