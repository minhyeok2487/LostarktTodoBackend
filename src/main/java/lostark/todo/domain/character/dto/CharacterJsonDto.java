package lostark.todo.domain.character.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.global.utils.DoubleDeserializer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterJsonDto {

    @JsonProperty("CharacterClassName")
    @ApiModelProperty(notes = "캐릭터 클래스")
    private String characterClassName;

    @JsonProperty("CharacterImage")
    @ApiModelProperty(notes = "캐릭터 이미지 url")
    private String characterImage;

    @JsonProperty("CharacterName")
    @ApiModelProperty(notes = "캐릭터 이름")
    private String characterName;

    @JsonProperty("ItemAvgLevel")
    @JsonDeserialize(using = DoubleDeserializer.class)
    @ApiModelProperty(notes = "캐릭터 아이템 레벨")
    private double itemAvgLevel;

    @JsonProperty("CombatPower")
    @JsonDeserialize(using = DoubleDeserializer.class)
    @ApiModelProperty(notes = "캐릭터 아이템 레벨")
    private double combatPower;

    @JsonProperty("CharacterLevel")
    private int characterLevel;

    @JsonProperty("ServerName")
    @ApiModelProperty(notes = "서버 이름")
    private String serverName;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("GuildName")
    private String guildName;

    @JsonProperty("TownName")
    private String townName;

    @JsonProperty("TownLevel")
    private Integer townLevel;

    @JsonProperty("ExpeditionLevel")
    private Integer expeditionLevel;

    @JsonProperty("Stats")
    private java.util.List<StatDto> stats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatDto {
        @JsonProperty("Type")
        private String type;

        @JsonProperty("Value")
        private String value;

        @JsonProperty("Tooltip")
        private java.util.List<String> tooltip;
    }
}
