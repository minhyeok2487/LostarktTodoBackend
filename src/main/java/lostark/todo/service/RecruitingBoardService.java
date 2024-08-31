package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.recruitingBoard.CreateRecruitingBoardRequest;
import lostark.todo.controller.dtoV2.recruitingBoard.SearchRecruitingBoardRequest;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.domain.recruitingBoard.RecruitingBoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
