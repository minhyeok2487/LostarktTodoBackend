package lostark.todo.domainMyGame.myevent.repository;

import lostark.todo.domainMyGame.myevent.entity.MyEventImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventImageRepository extends JpaRepository<MyEventImage, Long> {

    List<MyEventImage> findAllByIdIn(List<Long> ids);
}
