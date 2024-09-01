package lostark.todo.domain.recruitingBoard;


import lostark.todo.controller.dtoV2.recruitingBoard.SearchRecruitingBoardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface RecruitingBoardCustomRepository {

    Page<RecruitingBoard> search(SearchRecruitingBoardRequest request, PageRequest pageRequest);

    Optional<RecruitingBoard> get(long recruitingBoardId);
}
