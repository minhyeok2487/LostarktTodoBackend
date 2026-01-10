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

    private String sortBy = "characterId";  // 정렬 컬럼: characterId, memberId, serverName, characterName, characterClassName, itemLevel, createdDate
    private String sortDirection = "DESC";  // ASC or DESC
}
