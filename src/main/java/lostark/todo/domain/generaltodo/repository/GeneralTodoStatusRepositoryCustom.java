package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.dto.GeneralTodoStatusResponse;
import lostark.todo.domain.generaltodo.entity.GeneralTodoStatus;

import java.util.List;
import java.util.Optional;

public interface GeneralTodoStatusRepositoryCustom {

    Optional<GeneralTodoStatus> findByIdAndMemberId(Long statusId, Long memberId);

    Optional<GeneralTodoStatus> findFirstByCategory(Long categoryId, Long memberId);

    List<Long> findIdsByCategory(Long categoryId, Long memberId);

    void shiftSortOrders(Long categoryId, Long memberId, int startSortOrder);

    void updateSortOrders(Long categoryId, Long memberId, List<Long> orderedStatusIds);

    List<GeneralTodoStatusResponse> fetchResponses(Long memberId, String username);

    void deleteByIdSafe(Long id);
}
