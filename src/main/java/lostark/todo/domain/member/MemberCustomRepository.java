package lostark.todo.domain.member;

import lostark.todo.controller.adminDto.DashboardResponse;

import java.util.List;

public interface MemberCustomRepository {
    Member findMemberAndCharacters(String username);

    Member get(String username);

    List<DashboardResponse> searchMemberDashBoard(int limit);
}
