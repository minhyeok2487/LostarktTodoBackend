package lostark.todo.domainMyGame.myevent.repository;

import lostark.todo.domainMyGame.myevent.entity.MyEventVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventVideoRepository extends JpaRepository<MyEventVideo, Long> {
}
