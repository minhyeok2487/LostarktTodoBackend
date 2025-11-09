package lostark.todo.domain.servertodo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.servertodo.dto.ServerTodoOverviewResponse;
import lostark.todo.domain.servertodo.entity.ServerTodo;
import lostark.todo.domain.servertodo.entity.ServerTodoState;
import lostark.todo.domain.servertodo.repository.ServerTodoRepository;
import lostark.todo.domain.servertodo.repository.ServerTodoStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerTodoService {

    private final ServerTodoRepository serverTodoRepository;
    private final ServerTodoStateRepository serverTodoStateRepository;
    private final MemberRepository memberRepository;

    public ServerTodoOverviewResponse getServerTodos(String username) {
        Member member = memberRepository.get(username);
        List<ServerTodo> todos = serverTodoRepository.findAllVisible();
        List<String> serverNames = extractServerNames(member);
        List<ServerTodoState> states = serverNames.isEmpty()
                ? List.of()
                : serverTodoStateRepository.findByMemberAndServerNames(member.getId(), serverNames);
        return ServerTodoOverviewResponse.of(todos, states);
    }

    private List<String> extractServerNames(Member member) {
        Set<String> serverNames = member.getCharacters().stream()
                .filter(character -> !character.isDeleted())
                .map(Character::getServerName)
                .collect(Collectors.toSet());
        return new ArrayList<>(serverNames);
    }
}
