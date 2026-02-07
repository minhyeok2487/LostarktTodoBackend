package lostark.todo.domain.inspection.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.inspection.entity.InspectionCharacter;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionCharacterResponse {

    private long id;
    private String characterName;
    private String serverName;
    private String characterClassName;
    private String characterImage;
    private double itemLevel;
    private double combatPower;
    private String title;
    private String guildName;
    private String townName;
    private Integer townLevel;
    private Integer expeditionLevel;
    private int noChangeThreshold;

    @JsonProperty("isActive")
    private boolean isActive;
    private LocalDateTime createdDate;
    private Double previousCombatPower;
    private double combatPowerChange;
    private Double previousItemLevel;
    private double itemLevelChange;
    private long unchangedDays;

    public static InspectionCharacterResponse from(InspectionCharacter entity) {
        return InspectionCharacterResponse.builder()
                .id(entity.getId())
                .characterName(entity.getCharacterName())
                .serverName(entity.getServerName())
                .characterClassName(entity.getCharacterClassName())
                .characterImage(entity.getCharacterImage())
                .itemLevel(entity.getItemLevel())
                .combatPower(entity.getCombatPower())
                .title(entity.getTitle())
                .guildName(entity.getGuildName())
                .townName(entity.getTownName())
                .townLevel(entity.getTownLevel())
                .expeditionLevel(entity.getExpeditionLevel())
                .noChangeThreshold(entity.getNoChangeThreshold())
                .isActive(entity.isActive())
                .createdDate(entity.getCreatedDate())
                .build();
    }
}
