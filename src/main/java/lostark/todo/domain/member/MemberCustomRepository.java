package lostark.todo.domain.member;

import lostark.todo.controller.adminDto.DashboardMemberResponse;

import java.util.List;

public interface MemberCustomRepository {
    Member findMemberAndCharacters(String username);

    Member get(String username);

    List<DashboardMemberResponse> searchDashBoard(int limit);
}
