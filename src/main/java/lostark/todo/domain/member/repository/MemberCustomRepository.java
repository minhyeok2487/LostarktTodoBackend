package lostark.todo.domain.member.repository;

import lostark.todo.admin.dto.DashboardResponse;
import lostark.todo.admin.dto.SearchAdminMemberRequest;
import lostark.todo.admin.dto.SearchAdminMemberResponse;
import lostark.todo.domain.member.entity.Member;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface MemberCustomRepository {

    Member get(String username);

    Member get(Long id);

    List<DashboardResponse> searchMemberDashBoard(int limit);

    PageImpl<SearchAdminMemberResponse> searchAdminMember(SearchAdminMemberRequest request, PageRequest pageRequest);
}
