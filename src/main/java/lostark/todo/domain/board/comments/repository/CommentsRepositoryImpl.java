package lostark.todo.domain.board.comments.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.admin.QSearchAdminCommentsResponse;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsResponse;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;

import java.util.List;

import static lostark.todo.domain.member.entity.QMember.member;
import static lostark.todo.domain.board.comments.entity.QComments.comments;

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

    private BooleanExpression eqUsername(String username) {
        if (StringUtils.hasText(username)) {
            return member.username.contains(username);
        }
        return null;
    }
}
