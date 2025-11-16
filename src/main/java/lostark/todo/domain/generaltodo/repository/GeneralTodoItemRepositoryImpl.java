package lostark.todo.domain.generaltodo.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.GeneralTodoItemResponse;
import lostark.todo.domain.generaltodo.dto.QGeneralTodoItemResponse;
import lostark.todo.domain.generaltodo.entity.GeneralTodoItem;
import lostark.todo.domain.generaltodo.entity.QGeneralTodoItem;

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
}
