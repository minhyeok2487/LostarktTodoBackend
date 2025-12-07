package lostark.todo.domainMyGame.myevent.repository;

import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<MyEvent, String>, EventCustomRepository {
}
