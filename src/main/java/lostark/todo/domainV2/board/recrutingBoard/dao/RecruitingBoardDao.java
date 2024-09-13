package lostark.todo.domainV2.board.recrutingBoard.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.recrutingBoard.dto.SearchRecruitingBoardRequest;
import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoard;
import lostark.todo.domainV2.board.recrutingBoard.repository.RecruitingBoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lostark.todo.constants.ErrorMessages.RECRUITING_BOARD_NOT_FOUND;

@RequiredArgsConstructor
@Repository
public class RecruitingBoardDao {

    private final RecruitingBoardRepository recruitingBoardRepository;

    @Transactional(readOnly = true)
    public Page<RecruitingBoard> search(SearchRecruitingBoardRequest request, PageRequest pageRequest) {
        return recruitingBoardRepository.search(request, pageRequest);
    }

    @Transactional(readOnly = true)
    public RecruitingBoard get(long recruitingBoardId) {
        return recruitingBoardRepository.get(recruitingBoardId)
                .orElseThrow(() -> new IllegalArgumentException(RECRUITING_BOARD_NOT_FOUND));
    }

    @Transactional
    public RecruitingBoard save(RecruitingBoard recruitingBoard) {
        return recruitingBoardRepository.save(recruitingBoard);
    }

    @Transactional
    public void delete(RecruitingBoard recruitingBoard) {
        recruitingBoardRepository.delete(recruitingBoard);
    }

    @Transactional
    public List<RecruitingBoard> searchMain() {
        return recruitingBoardRepository.searchMain();
    }
}
