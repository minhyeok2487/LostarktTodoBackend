package lostark.todo.domainV2.board.community.repository;

import lostark.todo.domainV2.board.community.entity.CommunityImages;

import java.util.List;

public interface CommunityImagesCustomRepository{
    List<CommunityImages> search(List<Long> imageList);
}
