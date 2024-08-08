package lostark.todo.domain.member;

import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminMemberResponse;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface MemberCustomRepository {

    Optional<Member> get(String username);

    List<DashboardResponse> searchMemberDashBoard(int limit);

    PageImpl<SearchAdminMemberResponse> searchAdminMember(SearchAdminMemberRequest request, PageRequest pageRequest);
}
