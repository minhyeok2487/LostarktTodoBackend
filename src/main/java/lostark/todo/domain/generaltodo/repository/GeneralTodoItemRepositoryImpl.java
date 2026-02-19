package lostark.todo.domain.generaltodo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.GeneralTodoItemResponse;
import lostark.todo.domain.generaltodo.dto.QGeneralTodoItemResponse;
import lostark.todo.domain.generaltodo.dto.SearchGeneralTodoRequest;
import lostark.todo.domain.generaltodo.entity.GeneralTodoItem;
import lostark.todo.domain.generaltodo.entity.QGeneralTodoItem;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GeneralTodoItemRepositoryImpl implements GeneralTodoItemRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QGeneralTodoItem item = QGeneralTodoItem.generalTodoItem;

    @Override
    public Optional<GeneralTodoItem> findByIdAndMemberId(Long itemId, Long memberId) {
        return Optional.ofNullable(factory.selectFrom(item)
                .where(item.id.eq(itemId), item.member.id.eq(memberId))
                .fetchOne());
    }

    @Override
    public List<GeneralTodoItemResponse> fetchResponses(Long memberId, String username) {
        return factory.select(new QGeneralTodoItemResponse(
                        item.id,
                        item.title,
                        item.description,
                        item.folder.id,
                        item.category.id,
                        Expressions.constant(username),
                        item.startDate,
                        item.dueDate,
                        item.allDay,
                        item.status.id,
                        item.status.name,
                        item.createdDate,
                        item.lastModifiedDate
                ))
                .from(item)
                .where(item.member.id.eq(memberId))
                .orderBy(item.folder.id.asc(), item.category.id.asc(), item.createdDate.asc(), item.id.asc())
                .fetch();
    }

    @Override
    public List<GeneralTodoItemResponse> search(Long memberId, String username, SearchGeneralTodoRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(item.member.id.eq(memberId));

        if (StringUtils.hasText(request.getQuery())) {
            String query = request.getQuery().toLowerCase();
            builder.and(
                    item.title.lower().contains(query)
                            .or(item.description.lower().contains(query))
            );
        }

        if (request.getFolderId() != null) {
            builder.and(item.folder.id.eq(request.getFolderId()));
        }

        if (request.getCategoryId() != null) {
            builder.and(item.category.id.eq(request.getCategoryId()));
        }

        if (request.getStatusId() != null) {
            builder.and(item.status.id.eq(request.getStatusId()));
        }

        return factory.select(new QGeneralTodoItemResponse(
                        item.id,
                        item.title,
                        item.description,
                        item.folder.id,
                        item.category.id,
                        Expressions.constant(username),
                        item.startDate,
                        item.dueDate,
                        item.allDay,
                        item.status.id,
                        item.status.name,
                        item.createdDate,
                        item.lastModifiedDate
                ))
                .from(item)
                .where(builder)
                .orderBy(item.folder.id.asc(), item.category.id.asc(), item.createdDate.asc(), item.id.asc())
                .fetch();
    }

    @Override
    public void deleteByIdSafe(Long id) {
        factory.delete(item)
                .where(item.id.eq(id))
                .execute();
    }
}
