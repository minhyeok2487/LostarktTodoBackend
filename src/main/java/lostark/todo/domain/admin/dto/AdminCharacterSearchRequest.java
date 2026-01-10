package lostark.todo.domain.admin.dto;

import lombok.Data;

@Data
public class AdminCharacterSearchRequest {

    private Long memberId;
    private String serverName;
    private String characterName;
    private String characterClassName;
    private Double minItemLevel;
    private Double maxItemLevel;
    private Boolean isDeleted;
}
