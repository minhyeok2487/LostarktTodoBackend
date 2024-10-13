package lostark.todo.domainV2.character.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BaseCharacterRequest {
    @NotNull
    private long characterId;
}
