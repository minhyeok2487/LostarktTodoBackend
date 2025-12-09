package lostark.todo.domain.servertodo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.servertodo.dto.ServerTodoCheckRequest;
import lostark.todo.domain.servertodo.dto.ServerTodoCreateRequest;
import lostark.todo.domain.servertodo.dto.ServerTodoOverviewResponse;
import lostark.todo.domain.servertodo.dto.ServerTodoToggleEnabledRequest;
import lostark.todo.domain.servertodo.entity.ServerTodo;
import lostark.todo.domain.servertodo.entity.ServerTodoState;
import lostark.todo.domain.servertodo.repository.ServerTodoRepository;
import lostark.todo.domain.servertodo.repository.ServerTodoStateRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerTodoService {

    private final ServerTodoRepository serverTodoRepository;
    private final ServerTodoStateRepository serverTodoStateRepository;
    private final MemberRepository memberRepository;

    private static final String SERVER_TODO_NOT_FOUND = "등록된 서버 숙제가 아닙니다.";
    private static final String SERVER_NOT_BELONG_TO_MEMBER = "해당 서버에는 캐릭터가 없습니다.";
    private static final String SERVER_TODO_STATE_NOT_FOUND = "서버 숙제가 활성화되어 있지 않습니다.";

    @Transactional
    public ServerTodo createServerTodo(ServerTodoCreateRequest request) {
        ServerTodo serverTodo = ServerTodo.builder()
                .contentName(request.getContentName())
                .defaultEnabled(request.getDefaultEnabled())
                .visibleWeekdays(request.getVisibleWeekdays() != null
                    ? request.getVisibleWeekdays()
                    : EnumSet.noneOf(lostark.todo.domain.servertodo.enums.VisibleWeekday.class))
                .build();

        return serverTodoRepository.save(serverTodo);
    }

    @Transactional(readOnly = true)
    public ServerTodoOverviewResponse getServerTodos(String username) {
        Member member = memberRepository.get(username);
        List<String> serverNames = extractServerNames(member);
        return buildOverview(member, serverNames);
    }

    @Transactional
    public ServerTodoOverviewResponse toggleEnabled(String username, Long todoId, ServerTodoToggleEnabledRequest request) {
        Member member = memberRepository.get(username);
        ServerTodo todo = serverTodoRepository.findById(todoId)
                .orElseThrow(() -> new ConditionNotMetException(SERVER_TODO_NOT_FOUND));

        List<String> serverNames = extractServerNames(member);
        if (!serverNames.contains(request.getServerName())) {
            throw new ConditionNotMetException(SERVER_NOT_BELONG_TO_MEMBER);
        }

        ServerTodoState state = serverTodoStateRepository.findByMemberAndTodo(member.getId(), todoId, request.getServerName());
        if (state == null) {
            state = ServerTodoState.create(todo, member, request.getServerName(), todo.isDefaultEnabled());
        }

        state.updateEnabled(request.getEnabled());
        serverTodoStateRepository.save(state);

        return buildOverview(member, serverNames);
    }

    @Transactional
    public ServerTodoOverviewResponse updateChecked(String username, Long todoId, ServerTodoCheckRequest request) {
        Member member = memberRepository.get(username);

        List<String> serverNames = extractServerNames(member);
        if (!serverNames.contains(request.getServerName())) {
            throw new ConditionNotMetException(SERVER_NOT_BELONG_TO_MEMBER);
        }

        ServerTodoState state = serverTodoStateRepository.findByMemberAndTodo(member.getId(), todoId, request.getServerName());
        if (state == null) {
            throw new ConditionNotMetException(SERVER_TODO_STATE_NOT_FOUND);
        }

        state.updateChecked(request.getChecked());

        return buildOverview(member, serverNames);
    }

    private List<String> extractServerNames(Member member) {
        Set<String> serverNames = member.getCharacters().stream()
                .filter(character -> !character.isDeleted())
                .map(Character::getServerName)
                .collect(Collectors.toSet());
        return new ArrayList<>(serverNames);
    }

    private ServerTodoOverviewResponse buildOverview(Member member, List<String> serverNames) {
        List<ServerTodo> todos = serverTodoRepository.findAllVisible();
        List<ServerTodoState> states = serverNames.isEmpty()
                ? List.of()
                : serverTodoStateRepository.findByMemberAndServerNames(member.getId(), serverNames);
        return ServerTodoOverviewResponse.of(todos, states);
    }
}
