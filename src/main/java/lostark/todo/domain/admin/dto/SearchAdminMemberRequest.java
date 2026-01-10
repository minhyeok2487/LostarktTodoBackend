package lostark.todo.domain.admin.dto;

import lombok.Data;
import lostark.todo.domain.admin.enums.MemberSortBy;
import org.springframework.data.domain.Sort;

@Data
public class SearchAdminMemberRequest {

    private String username;

    private String authProvider;

    private String mainCharacter;

    private MemberSortBy sortBy = MemberSortBy.CREATED_DATE;

    private Sort.Direction sortDirection = Sort.Direction.DESC;
}
