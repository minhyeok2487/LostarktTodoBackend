package lostark.todo.domain.generaltodo.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.GeneralTodoStatusResponse;
import lostark.todo.domain.generaltodo.dto.QGeneralTodoStatusResponse;
import lostark.todo.domain.generaltodo.entity.GeneralTodoStatus;
import lostark.todo.domain.generaltodo.entity.QGeneralTodoStatus;
import lostark.todo.domain.generaltodo.enums.GeneralTodoStatusType;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class GeneralTodoStatusRepositoryImpl implements GeneralTodoStatusRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QGeneralTodoStatus status = QGeneralTodoStatus.generalTodoStatus;

    @Override
    public Optional<GeneralTodoStatus> findByIdAndMemberId(Long statusId, Long memberId) {
        return Optional.ofNullable(factory.selectFrom(status)
                .where(status.id.eq(statusId), status.member.id.eq(memberId))
                .fetchOne());
    }

    @Override
    public List<Long> findIdsByCategory(Long categoryId, Long memberId) {
        return factory.select(status.id)
                .from(status)
                .where(status.category.id.eq(categoryId), status.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public int getNextSortOrder(Long categoryId, Long memberId) {
        Integer maxOrder = factory.select(status.sortOrder.max())
                .from(status)
                .where(status.category.id.eq(categoryId), status.member.id.eq(memberId))
                .fetchOne();
        return maxOrder == null ? 0 : maxOrder + 1;
    }

    @Override
    public void shiftSortOrders(Long categoryId, Long memberId, int startSortOrder) {
        factory.update(status)
                .set(status.sortOrder, status.sortOrder.add(1))
                .where(status.category.id.eq(categoryId),
                        status.member.id.eq(memberId),
                        status.sortOrder.goe(startSortOrder))
                .execute();
    }

    @Override
    public void updateSortOrders(Long categoryId, Long memberId, List<Long> orderedStatusIds) {
        IntStream.range(0, orderedStatusIds.size())
                .forEach(index -> factory.update(status)
                        .set(status.sortOrder, index)
                        .where(status.category.id.eq(categoryId),
                                status.member.id.eq(memberId),
                                status.id.eq(orderedStatusIds.get(index)))
                        .execute());
    }

    @Override
    public List<GeneralTodoStatusResponse> fetchResponses(Long memberId, String username) {
        return factory.select(new QGeneralTodoStatusResponse(
                        status.id,
                        status.category.id,
                        Expressions.constant(username),
                        status.name,
                        status.sortOrder,
                        status.type
                ))
                .from(status)
                .where(status.member.id.eq(memberId))
                .orderBy(status.category.id.asc(),
                        status.sortOrder.asc(),
                        status.id.asc())
                .fetch();
    }

    @Override
    public Optional<GeneralTodoStatus> findByCategoryAndType(Long categoryId, Long memberId, GeneralTodoStatusType type) {
        return Optional.ofNullable(factory.selectFrom(status)
                .where(status.category.id.eq(categoryId),
                        status.member.id.eq(memberId),
                        status.type.eq(type))
                .fetchFirst());
    }

    @Override
    public Optional<GeneralTodoStatus> findFirstProgressByCategory(Long categoryId, Long memberId) {
        return Optional.ofNullable(factory.selectFrom(status)
                .where(status.category.id.eq(categoryId),
                        status.member.id.eq(memberId),
                        status.type.eq(GeneralTodoStatusType.PROGRESS))
                .orderBy(status.sortOrder.asc(), status.id.asc())
                .fetchFirst());
    }
}
