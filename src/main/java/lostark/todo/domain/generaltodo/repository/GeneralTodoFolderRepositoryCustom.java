package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;

import java.util.List;

import java.util.Optional;

public interface GeneralTodoFolderRepositoryCustom {
    Optional<GeneralTodoFolder> findByIdAndMemberId(Long folderId, Long memberId);

    List<Long> findIdsByMemberId(Long memberId);

    void shiftSortOrders(Long memberId, int startSortOrder);

    void updateSortOrders(Long memberId, List<Long> orderedFolderIds);

    List<GeneralTodoFolder> findAllWithCategoriesByMemberId(Long memberId);
}