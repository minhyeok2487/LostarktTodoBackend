package lostark.todo.domain.admin.dto;

import lombok.Data;

@Data
public class AdminCharacterUpdateRequest {

    private String characterName;
    private Double itemLevel;
    private Integer sortNumber;
    private String memo;
    private Boolean goldCharacter;
    private Boolean isDeleted;
}
