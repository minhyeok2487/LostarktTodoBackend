package lostark.todo.domainMyGame.myevent.repository;

import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import lostark.todo.domainMyGame.myevent.enums.MyEventType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCustomRepository {

    MyEvent get(Long id);

    PageImpl<MyEvent> searchEvents(List<Long> gameIds, LocalDateTime startDate, LocalDateTime endDate,
                                   MyEventType type, PageRequest pageRequest);
}
