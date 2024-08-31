package lostark.todo.domain.recruitingBoard;


import lostark.todo.controller.dtoV2.recruitingBoard.SearchRecruitingBoardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RecruitingBoardCustomRepository {

    Page<RecruitingBoard> search(SearchRecruitingBoardRequest request, PageRequest pageRequest);
}
