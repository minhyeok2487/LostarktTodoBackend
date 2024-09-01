package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.recruitingBoard.CreateRecruitingBoardRequest;
import lostark.todo.controller.dtoV2.recruitingBoard.SearchRecruitingBoardRequest;
import lostark.todo.controller.dtoV2.recruitingBoard.UpdateRecruitingBoardRequest;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.domain.recruitingBoard.RecruitingBoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static lostark.todo.constants.ErrorMessages.MEMBER_NOT_MATCH;
import static lostark.todo.constants.ErrorMessages.RECRUITING_BOARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecruitingBoardService {

    private final RecruitingBoardRepository recruitingBoardRepository;

    @Transactional
    public RecruitingBoard create(Member member, CreateRecruitingBoardRequest request) {
        RecruitingBoard recruitingBoard = RecruitingBoard.toEntity(member, request);
        return recruitingBoardRepository.save(recruitingBoard);
    }

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
    public void upShowCount(RecruitingBoard recruitingBoard) {
        recruitingBoard.upShowCount();
    }

    private RecruitingBoard validateOwnership(Member member, Long recruitingBoardId) {
        RecruitingBoard recruitingBoard = get(recruitingBoardId);
        if (member.getId() != recruitingBoard.getMember().getId()) {
            throw new IllegalArgumentException(MEMBER_NOT_MATCH);
        }
        return recruitingBoard;
    }

    @Transactional
    public void update(Member member, UpdateRecruitingBoardRequest request, Long recruitingBoardId) {
        RecruitingBoard recruitingBoard = validateOwnership(member, recruitingBoardId);
        recruitingBoard.update(request);
    }

    @Transactional
    public void delete(Member member, Long recruitingBoardId) {
        RecruitingBoard recruitingBoard = validateOwnership(member, recruitingBoardId);
        recruitingBoardRepository.delete(recruitingBoard);
    }

}
