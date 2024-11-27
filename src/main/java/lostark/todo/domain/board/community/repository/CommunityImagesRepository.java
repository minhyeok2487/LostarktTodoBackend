package lostark.todo.domain.board.community.repository;

import lostark.todo.domain.board.community.entity.CommunityImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityImagesRepository
        extends JpaRepository<CommunityImages, Long>, CommunityImagesCustomRepository {
}
