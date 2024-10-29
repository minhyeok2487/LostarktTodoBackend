package lostark.todo.domainV2.board.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.entity.CommunityImages;

import java.util.List;

import static lostark.todo.domainV2.board.community.entity.QCommunityImages.communityImages;


@RequiredArgsConstructor
public class CommunityImagesRepositoryImpl implements CommunityImagesCustomRepository {

    private final JPAQueryFactory factory;


    @Override
    public List<CommunityImages> search(List<Long> imageList) {
        return factory.select(communityImages)
                .from(communityImages)
                .where(
                        communityImages.id.in(imageList)
                ).fetch();
    }
}
