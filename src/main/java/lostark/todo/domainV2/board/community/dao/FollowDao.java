package lostark.todo.domainV2.board.community.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.repository.FollowRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional
public class FollowDao {

    private final FollowRepository repository;
}
