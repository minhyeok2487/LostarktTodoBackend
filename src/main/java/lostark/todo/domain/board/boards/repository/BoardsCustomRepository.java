package lostark.todo.domain.board.boards.repository;

import lostark.todo.domain.board.boards.entity.Boards;

import java.util.List;

public interface BoardsCustomRepository {

    List<Boards> search();
}