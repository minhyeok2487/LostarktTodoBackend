package lostark.todo.domain.board.comments.repository;

import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface CommentsCustomRepository {

    Page<SearchAdminCommentsResponse> searchAdmin(SearchAdminCommentsRequest request, PageRequest pageRequest);
}
