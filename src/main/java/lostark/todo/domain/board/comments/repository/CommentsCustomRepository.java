package lostark.todo.domain.board.comments.repository;

public interface CommentsCustomRepository {

    void deleteByIdSafe(Long id);

    void deleteByParentIdSafe(Long parentId);
}
