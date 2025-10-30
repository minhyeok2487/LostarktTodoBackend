package lostark.todo.domain.generaltodo.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.generaltodo.dto.GeneralTodoFolderResponse;
import lostark.todo.domain.generaltodo.dto.QGeneralTodoFolderResponse;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
import lostark.todo.domain.generaltodo.entity.QGeneralTodoFolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class GeneralTodoFolderRepositoryImpl implements GeneralTodoFolderRepositoryCustom {

    private final JPAQueryFactory factory;
    private final QGeneralTodoFolder folder = QGeneralTodoFolder.generalTodoFolder;

    @Override
    public Optional<GeneralTodoFolder> findByIdAndMemberId(Long folderId, Long memberId) {
        return Optional.ofNullable(factory.selectFrom(folder)
                .where(folder.id.eq(folderId), folder.member.id.eq(memberId))
                .fetchOne());
    }

    @Override
    public int getNextSortOrder(Long memberId) {
        Integer maxOrder = factory.select(folder.sortOrder.max())
                .from(folder)
                .where(folder.member.id.eq(memberId))
                .fetchOne();
        return maxOrder == null ? 0 : maxOrder + 1;
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
    public List<Long> findIdsByMemberId(Long memberId) {
        return factory.select(folder.id)
                .from(folder)
                .where(folder.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public List<GeneralTodoFolderResponse> fetchResponses(Long memberId, String username) {
        return factory.select(new QGeneralTodoFolderResponse(
                        folder.id,
                        folder.name,
                        Expressions.constant(username),
                        folder.sortOrder
                ))
                .from(folder)
                .where(folder.member.id.eq(memberId))
                .orderBy(folder.sortOrder.asc(), folder.id.asc())
                .fetch();
    }
}
