package lostark.todo.domainV2.board.recrutingBoard.repository;


import lostark.todo.domainV2.board.recrutingBoard.dto.SearchRecruitingBoardRequest;
import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface RecruitingBoardCustomRepository {

    Page<RecruitingBoard> search(SearchRecruitingBoardRequest request, PageRequest pageRequest);

    Optional<RecruitingBoard> get(long recruitingBoardId);
}
