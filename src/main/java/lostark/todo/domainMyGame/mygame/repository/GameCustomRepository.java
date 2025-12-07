package lostark.todo.domainMyGame.mygame.repository;

import lostark.todo.domainMyGame.mygame.entity.MyGame;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public interface GameCustomRepository {

    MyGame get(String id);

    PageImpl<MyGame> searchGames(String search, PageRequest pageRequest);
}
