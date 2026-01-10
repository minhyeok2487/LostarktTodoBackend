package lostark.todo.domain.admin.dto;

import lombok.Data;

@Data
public class SearchAdminMemberRequest {

    private String username;

    private String authProvider;

    private String mainCharacter;

    private String sortBy = "createdDate";  // 정렬 컬럼: memberId, username, createdDate, authProvider, mainCharacter

    private String sortDirection = "DESC";  // ASC or DESC
}
