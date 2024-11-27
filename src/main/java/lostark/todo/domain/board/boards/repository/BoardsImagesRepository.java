package lostark.todo.domain.board.boards.repository;

import lostark.todo.domain.board.boards.entity.BoardImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardsImagesRepository extends JpaRepository<BoardImages, Long> {

    List<BoardImages> findAllByFileNameIn(List<String> urls);
}
