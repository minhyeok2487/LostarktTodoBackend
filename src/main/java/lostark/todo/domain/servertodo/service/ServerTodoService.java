package lostark.todo.domain.servertodo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.servertodo.repository.ServerTodoRepository;
import lostark.todo.domain.servertodo.repository.ServerTodoStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerTodoService {

    private final ServerTodoRepository serverTodoRepository;
    private final ServerTodoStateRepository serverTodoStateRepository;

}
