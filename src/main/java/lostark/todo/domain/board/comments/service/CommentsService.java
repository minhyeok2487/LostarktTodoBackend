package lostark.todo.domain.board.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsResponse;
import lostark.todo.domain.board.comments.entity.Comments;
import lostark.todo.domain.board.comments.repository.CommentsRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentsService {

    private final CommentsRepository commentsRepository;

    public Page<Comments> findAllByParentIdIs0(int page) {
        return commentsRepository.findAllByParentIdIs0(PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdDate")));
    }

    public List<Comments> findAllByParentId(long id) {
        return commentsRepository.findAllByParentId(id);
    }

    @Transactional(readOnly = true)
    public Page<SearchAdminCommentsResponse> searchAdmin(SearchAdminCommentsRequest request, PageRequest pageRequest) {
        return commentsRepository.searchAdmin(request, pageRequest);
    }
}
