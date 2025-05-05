package lostark.todo.domain.admin.dto;

import lombok.Data;

@Data
public class SearchAdminMemberRequest {

    private String username;

    private String authProvider;

    private String mainCharacter;
}
