package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.dto.GeneralTodoCategoryResponse;
import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;

import java.util.List;
import java.util.Optional;

public interface GeneralTodoCategoryRepositoryCustom {

    Optional<GeneralTodoCategory> findByIdAndMemberId(Long categoryId, Long memberId);

    List<Long> findIdsByFolder(Long folderId, Long memberId);

    int getNextSortOrder(Long folderId, Long memberId);

    void shiftSortOrders(Long folderId, Long memberId, int startSortOrder);

    void updateSortOrders(Long folderId, Long memberId, List<Long> orderedCategoryIds);

    List<GeneralTodoCategoryResponse> fetchResponses(Long memberId, String username);

    void deleteByIdSafe(Long id);
}
