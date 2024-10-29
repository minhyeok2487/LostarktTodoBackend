package lostark.todo.domainV2.board.community.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.repository.CommunityImagesRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Repository
@Transactional
public class CommunityImagesDao {

    private final CommunityImagesRepository repository;

    @Transactional
    public void updateAll(long communityId, List<Long> imageList) {
        AtomicInteger counter = new AtomicInteger(1);
        repository.search(imageList)
                .forEach(image -> image.update(communityId, counter.getAndIncrement()));
    }
}
