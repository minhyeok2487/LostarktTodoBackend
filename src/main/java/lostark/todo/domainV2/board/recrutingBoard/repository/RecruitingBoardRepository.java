package lostark.todo.domainV2.board.recrutingBoard.repository;

import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitingBoardRepository extends JpaRepository<RecruitingBoard, Long>, RecruitingBoardCustomRepository {
}
