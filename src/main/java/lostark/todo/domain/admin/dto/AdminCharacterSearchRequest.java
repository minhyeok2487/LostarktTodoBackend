package lostark.todo.domain.admin.dto;

import lombok.Data;
import lostark.todo.domain.admin.enums.CharacterSortBy;
import org.springframework.data.domain.Sort;

@Data
public class AdminCharacterSearchRequest {

    private Long memberId;
    private String serverName;
    private String characterName;
    private String characterClassName;
    private Double minItemLevel;
    private Double maxItemLevel;
    private Boolean isDeleted;

    private CharacterSortBy sortBy = CharacterSortBy.CHARACTER_ID;
    private Sort.Direction sortDirection = Sort.Direction.DESC;
}
