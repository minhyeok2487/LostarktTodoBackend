package lostark.todo.domainV2.board.comments.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.admin.QSearchAdminCommentsResponse;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsResponse;
import lostark.todo.domainV2.board.comments.dto.CommentResponse;
import lostark.todo.domainV2.board.comments.entity.QComments;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;

import java.util.List;

import static lostark.todo.domain.member.QMember.member;
import static lostark.todo.domainV2.board.comments.entity.QComments.comments;

@RequiredArgsConstructor
public class CommentsRepositoryImpl implements CommentsCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public Page<SearchAdminCommentsResponse> searchAdmin(SearchAdminCommentsRequest request, PageRequest pageRequest) {
        List<SearchAdminCommentsResponse> list = factory.select(new QSearchAdminCommentsResponse(comments))
                .from(comments)
                .leftJoin(comments.member, member).fetchJoin()
                .where(
                        eqUsername(request.getUsername())
                )
                .orderBy(comments.createdDate.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        long total = factory.selectFrom(comments)
                .orderBy(comments.createdDate.desc())
                .leftJoin(comments.member, member).fetchJoin()
                .where(eqUsername(request.getUsername()))
                .fetchCount();

        return new PageImpl<>(list, pageRequest, total);
    }

    @Override
    public CursorResponse<CommentResponse> searchCursor(Long commentsId, PageRequest pageRequest) {
        QComments commentsSub = new QComments("commentsSub");



        List<CommentResponse> fetch = factory.select(
                        Projections.fields(CommentResponse.class,
                                comments.id.as("commentId"),
                                comments.body.as("body"),
                                member.username.as("username"),
                                comments.createdDate.as("regDate"),
                                member.id.as("memberId"),
                                Expressions.as(JPAExpressions
                                        .select(commentsSub.count())
                                        .from(commentsSub)
                                        .where(commentsSub.parentId.eq(comments.id)), "commentCount"),
                                member.role.as("role")
                        ))
                .from(comments)
                .leftJoin(comments.member, member)
                .where(
                        ltCommentsId(commentsId),
                        eqParentId(0L)
                )
                .orderBy(comments.id.desc())
                .limit(pageRequest.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if (fetch.size() > pageRequest.getPageSize()) {
            fetch.remove(pageRequest.getPageSize());
            hasNext = true;
        }

        return new CursorResponse<>(fetch, hasNext);

    }


    private BooleanExpression eqUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.contains(username);
        }
        return null;
    }

    private BooleanExpression ltCommentsId(Long commentsId) {
        if (commentsId != null) {
            return comments.id.lt(commentsId);
        }
        return null;
    }

    private BooleanExpression eqParentId(Long commentsId) {
        if (commentsId != null) {
            return comments.parentId.eq(commentsId);
        }
        return null;
    }
}
