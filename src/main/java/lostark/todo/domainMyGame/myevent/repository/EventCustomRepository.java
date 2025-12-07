package lostark.todo.domainMyGame.myevent.repository;

import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCustomRepository {

    MyEvent get(String id);

    PageImpl<MyEvent> searchEvents(List<String> gameIds, LocalDateTime startDate, LocalDateTime endDate,
                                  String type, PageRequest pageRequest);
}
