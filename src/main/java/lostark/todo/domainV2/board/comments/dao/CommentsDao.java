package lostark.todo.domainV2.board.comments.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.comments.dto.CommentResponse;
import lostark.todo.domainV2.board.comments.repository.CommentsRepository;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class CommentsDao {

    private final CommentsRepository commentsRepository;

    @Transactional(readOnly = true)
    public CursorResponse<CommentResponse> searchCursor(Long commentsId, PageRequest pageRequest) {
        return commentsRepository.searchCursor(commentsId, pageRequest);
    }
}
