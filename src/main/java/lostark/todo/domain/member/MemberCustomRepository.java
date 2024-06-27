package lostark.todo.domain.member;

import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberResponse;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface MemberCustomRepository {
    Member findMemberAndCharacters(String username);

    Member get(String username);

    List<DashboardResponse> searchMemberDashBoard(int limit);

    PageImpl<SearchAdminMemberResponse> searchAdminMember(SearchAdminMemberRequest request, PageRequest pageRequest);
}
