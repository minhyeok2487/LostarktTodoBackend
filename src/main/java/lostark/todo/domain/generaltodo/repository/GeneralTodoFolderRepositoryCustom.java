package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.dto.GeneralTodoFolderResponse;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;

import java.util.List;
import java.util.Optional;

public interface GeneralTodoFolderRepositoryCustom {

    Optional<GeneralTodoFolder> findByIdAndMemberId(Long folderId, Long memberId);

    int getNextSortOrder(Long memberId);

    void shiftSortOrders(Long memberId, int startSortOrder);

    void updateSortOrders(Long memberId, List<Long> orderedFolderIds);

    List<Long> findIdsByMemberId(Long memberId);

    List<GeneralTodoFolderResponse> fetchResponses(Long memberId, String username);
}
