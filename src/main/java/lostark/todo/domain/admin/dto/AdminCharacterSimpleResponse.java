package lostark.todo.domain.admin.dto;

import lombok.Builder;
import lombok.Data;
import lostark.todo.domain.character.entity.Character;

@Data
@Builder
public class AdminCharacterSimpleResponse {

    private long characterId;
    private String characterName;
    private String serverName;
    private double itemLevel;
    private String characterClassName;

    public static AdminCharacterSimpleResponse from(Character character) {
        return AdminCharacterSimpleResponse.builder()
                .characterId(character.getId())
                .characterName(character.getCharacterName())
                .serverName(character.getServerName())
                .itemLevel(character.getItemLevel())
                .characterClassName(character.getCharacterClassName())
                .build();
    }
}
