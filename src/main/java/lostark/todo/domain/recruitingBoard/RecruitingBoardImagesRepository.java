package lostark.todo.domain.recruitingBoard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitingBoardImagesRepository extends JpaRepository<RecruitingBoardImages, Long> {
    List<RecruitingBoardImages> findAllByFileNameIn(List<String> fileNameList);
}
