package lostark.todo.domain.generaltodo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
import lostark.todo.domain.generaltodo.entity.QGeneralTodoCategory;
import lostark.todo.domain.generaltodo.entity.QGeneralTodoFolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class GeneralTodoFolderRepositoryImpl implements GeneralTodoFolderRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QGeneralTodoFolder folder = QGeneralTodoFolder.generalTodoFolder;
    private final QGeneralTodoCategory category = QGeneralTodoCategory.generalTodoCategory;

    @Override
    public List<Long> findIdsByMemberId(Long memberId) {
        return factory.select(folder.id)
                .from(folder)
                .where(folder.member.id.eq(memberId))
                .orderBy(folder.sortOrder.asc(), folder.id.asc())
                .fetch();
    }

    @Override
    public void shiftSortOrders(Long memberId, int startSortOrder) {
        factory.update(folder)
                .set(folder.sortOrder, folder.sortOrder.add(1))
                .where(folder.member.id.eq(memberId), folder.sortOrder.goe(startSortOrder))
                .execute();
    }

    @Override
    public void updateSortOrders(Long memberId, List<Long> orderedFolderIds) {
        IntStream.range(0, orderedFolderIds.size())
                .forEach(index -> factory.update(folder)
                        .set(folder.sortOrder, index)
                        .where(folder.member.id.eq(memberId), folder.id.eq(orderedFolderIds.get(index)))
                        .execute());
    }

    @Override
    public Optional<GeneralTodoFolder> findByIdAndMemberId(Long folderId, Long memberId) {
        return Optional.ofNullable(factory.selectFrom(folder)
                .where(folder.id.eq(folderId), folder.member.id.eq(memberId))
                .fetchOne());
    }

    @Override
    public List<GeneralTodoFolder> findAllWithCategoriesByMemberId(Long memberId) {
        return factory.selectFrom(folder).distinct()
                .leftJoin(folder.categories, category).fetchJoin()
                .where(folder.member.id.eq(memberId))
                .orderBy(folder.sortOrder.asc(), category.sortOrder.asc())
                .fetch();
    }
}