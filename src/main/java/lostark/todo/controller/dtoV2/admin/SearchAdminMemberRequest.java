package lostark.todo.controller.dtoV2.admin;

import lombok.Data;

@Data
public class SearchAdminMemberRequest {

    private String username;

    private String authProvider;

    private String mainCharacter;
}
