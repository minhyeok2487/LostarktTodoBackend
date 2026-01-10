package lostark.todo.domain.admin.dto;

import lombok.Builder;
import lombok.Data;
import lostark.todo.domain.character.entity.Character;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminCharacterResponse {

    private long characterId;
    private Long memberId;  // null이면 회원 정보 없음
    private String memberUsername;
    private String serverName;
    private String characterName;
    private int characterLevel;
    private String characterClassName;
    private String characterImage;
    private double itemLevel;
    private int sortNumber;
    private boolean goldCharacter;
    private boolean isDeleted;
    private LocalDateTime createdDate;

    public static AdminCharacterResponse from(Character character) {
        return AdminCharacterResponse.builder()
                .characterId(character.getId())
                .memberId(character.getMember() != null ? character.getMember().getId() : null)
                .memberUsername(character.getMember() != null ? character.getMember().getUsername() : null)
                .serverName(character.getServerName())
                .characterName(character.getCharacterName())
                .characterLevel(character.getCharacterLevel())
                .characterClassName(character.getCharacterClassName())
                .characterImage(character.getCharacterImage())
                .itemLevel(character.getItemLevel())
                .sortNumber(character.getSortNumber())
                .goldCharacter(character.isGoldCharacter())
                .isDeleted(character.isDeleted())
                .createdDate(character.getCreatedDate())
                .build();
    }
}
