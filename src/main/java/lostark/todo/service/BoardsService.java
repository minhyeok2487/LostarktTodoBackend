package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.boards.BoardsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardsService {

    private final BoardsRepository boardsRepository;

    public Page<Boards> findAll(int page) {
        return boardsRepository.findAllIsNoticeForce(PageRequest.of(page, 10));
    }

    public Boards find(long no) {
        return boardsRepository.findById(no).orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));
    }

    public Boards save(Boards boards) {
        return boardsRepository.save(boards);
    }


    public List<Boards> findAllByNoticeIsTrue() {
        return boardsRepository.findAllByNoticeIsTrue();
    }
}
