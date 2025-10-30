package lostark.todo.domain.generaltodo.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.GeneralTodoCategoryResponse;
import lostark.todo.domain.generaltodo.dto.QGeneralTodoCategoryResponse;
import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;
import lostark.todo.domain.generaltodo.entity.QGeneralTodoCategory;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class GeneralTodoCategoryRepositoryImpl implements GeneralTodoCategoryRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QGeneralTodoCategory category = QGeneralTodoCategory.generalTodoCategory;

    @Override
    public Optional<GeneralTodoCategory> findByIdAndMemberId(Long categoryId, Long memberId) {
        return Optional.ofNullable(factory.selectFrom(category)
                .where(category.id.eq(categoryId), category.member.id.eq(memberId))
                .fetchOne());
    }

    @Override
    public List<Long> findIdsByFolder(Long folderId, Long memberId) {
        return factory.select(category.id)
                .from(category)
                .where(category.folder.id.eq(folderId), category.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public int getNextSortOrder(Long folderId, Long memberId) {
        Integer maxOrder = factory.select(category.sortOrder.max())
                .from(category)
                .where(category.folder.id.eq(folderId), category.member.id.eq(memberId))
                .fetchOne();
        return maxOrder == null ? 0 : maxOrder + 1;
    }

    @Override
    public void shiftSortOrders(Long folderId, Long memberId, int startSortOrder) {
        factory.update(category)
                .set(category.sortOrder, category.sortOrder.add(1))
                .where(category.folder.id.eq(folderId), category.member.id.eq(memberId), category.sortOrder.goe(startSortOrder))
                .execute();
    }

    @Override
    public void updateSortOrders(Long folderId, Long memberId, List<Long> orderedCategoryIds) {
        IntStream.range(0, orderedCategoryIds.size())
                .forEach(index -> factory.update(category)
                        .set(category.sortOrder, index)
                        .where(category.folder.id.eq(folderId), category.member.id.eq(memberId), category.id.eq(orderedCategoryIds.get(index)))
                        .execute());
    }

    @Override
    public List<GeneralTodoCategoryResponse> fetchResponses(Long memberId, String username) {
        return factory.select(new QGeneralTodoCategoryResponse(
                        category.id,
                        category.name,
                        category.color,
                        category.folder.id,
                        Expressions.constant(username),
                        category.sortOrder,
                        category.viewMode
                ))
                .from(category)
                .where(category.member.id.eq(memberId))
                .orderBy(category.folder.id.asc(), category.sortOrder.asc(), category.id.asc())
                .fetch();
    }
}
