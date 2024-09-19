package lostark.todo.domainV2.board.comments.repository;

import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsResponse;
import lostark.todo.domainV2.board.comments.dto.CommentResponse;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface CommentsCustomRepository {

    Page<SearchAdminCommentsResponse> searchAdmin(SearchAdminCommentsRequest request, PageRequest pageRequest);

    CursorResponse<CommentResponse> searchCursor(Long commentsId, PageRequest pageRequest);
}
