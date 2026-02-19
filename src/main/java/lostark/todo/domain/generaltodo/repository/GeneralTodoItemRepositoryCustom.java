package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.dto.GeneralTodoItemResponse;
import lostark.todo.domain.generaltodo.dto.SearchGeneralTodoRequest;
import lostark.todo.domain.generaltodo.entity.GeneralTodoItem;

import java.util.List;
import java.util.Optional;

public interface GeneralTodoItemRepositoryCustom {

    Optional<GeneralTodoItem> findByIdAndMemberId(Long itemId, Long memberId);

    List<GeneralTodoItemResponse> fetchResponses(Long memberId, String username);

    List<GeneralTodoItemResponse> search(Long memberId, String username, SearchGeneralTodoRequest request);

    void deleteByIdSafe(Long id);
}
