package lostark.todo.domain.boards;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardsImagesRepository extends JpaRepository<BoardImages, Long> {

    List<BoardImages> findAllByFileNameIn(List<String> urls);

    List<BoardImages> findAllByBoardsIsNull();


}
