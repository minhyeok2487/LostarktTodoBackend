package lostark.todo.domainMyGame.mygame.repository;

import lostark.todo.domainMyGame.mygame.entity.MyGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<MyGame, String>, GameCustomRepository {
}
