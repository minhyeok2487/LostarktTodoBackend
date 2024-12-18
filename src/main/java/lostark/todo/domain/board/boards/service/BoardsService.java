package lostark.todo.domain.board.boards.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.board.boards.entity.Boards;
import lostark.todo.domain.board.boards.repository.BoardsRepository;
import org.springframework.cache.annotation.Cacheable;
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

    public Page<Boards> findAllByNoticeFalse(int page) {
        return boardsRepository.findAllByNoticeFalse(PageRequest.of(page, 10));
    }

    public Page<Boards> findAll(int page, int size) {
        return boardsRepository.findAll(PageRequest.of(page, size));
    }

    public Boards findById(long id) {
        return boardsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));
    }

    public List<Boards> findAllByNoticeIsTrue() {
        return boardsRepository.findAllByNoticeIsTrue();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "boardStore")
    public List<Boards> search() {
        return boardsRepository.search();
    }
}
