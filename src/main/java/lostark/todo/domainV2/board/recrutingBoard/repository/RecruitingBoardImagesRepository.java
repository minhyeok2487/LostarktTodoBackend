package lostark.todo.domainV2.board.recrutingBoard.repository;

import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoardImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitingBoardImagesRepository extends JpaRepository<RecruitingBoardImages, Long> {
    List<RecruitingBoardImages> findAllByFileNameIn(List<String> fileNameList);
}
