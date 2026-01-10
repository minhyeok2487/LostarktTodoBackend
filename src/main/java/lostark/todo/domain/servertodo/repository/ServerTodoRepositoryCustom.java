package lostark.todo.domain.servertodo.repository;

import lostark.todo.domain.servertodo.entity.ServerTodo;

import java.util.List;

public interface ServerTodoRepositoryCustom {

    // 관리자 생성(공용) + 해당 사용자가 생성한 서버 숙제 조회
    List<ServerTodo> findAllVisible(Long memberId);
}
