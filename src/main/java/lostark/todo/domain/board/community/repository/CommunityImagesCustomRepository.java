package lostark.todo.domain.board.community.repository;

import lostark.todo.domain.board.community.entity.CommunityImages;

import java.util.List;

public interface CommunityImagesCustomRepository{
    List<CommunityImages> search(List<Long> imageList);
}
