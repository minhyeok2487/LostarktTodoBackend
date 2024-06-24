package lostark.todo.domain.boards;

import java.util.List;

public interface BoardsCustomRepository {

    List<Boards> search();
}