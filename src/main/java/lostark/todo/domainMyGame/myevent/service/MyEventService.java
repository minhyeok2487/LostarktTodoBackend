package lostark.todo.domainMyGame.myevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainMyGame.myevent.dto.MyEventResponse;
import lostark.todo.domainMyGame.myevent.entity.MyEvent;
import lostark.todo.domainMyGame.myevent.repository.EventRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyEventService {

    private final EventRepository eventRepository;

    public MyEvent get(String id) {
        MyEvent event = eventRepository.get(id);
        if (event == null) {
            throw new ConditionNotMetException("존재하지 않는 이벤트입니다.");
        }
        return event;
    }

    public MyEventResponse getEventById(String id) {
        MyEvent event = get(id);
        return MyEventResponse.from(event);
    }

    public Page<MyEventResponse> searchEvents(List<String> gameIds,
                                              LocalDateTime startDate,
                                              LocalDateTime endDate,
                                              String type,
                                              int page,
                                              int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<MyEvent> eventsPage = eventRepository.searchEvents(gameIds, startDate, endDate, type, pageRequest);
        return eventsPage.map(MyEventResponse::from);
    }
}
