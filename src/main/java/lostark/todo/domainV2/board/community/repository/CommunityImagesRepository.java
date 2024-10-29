package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.board.community.entity.CommunityImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityImagesRepository
        extends JpaRepository<CommunityImages, Long>, CommunityImagesCustomRepository {
}
