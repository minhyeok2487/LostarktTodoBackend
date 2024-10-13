package lostark.todo.domainV2.character.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateDayGaugeRequest extends BaseCharacterRequest {

    private long characterId;

    private Integer eponaCheck;

    private Integer eponaGauge;

    private Integer chaosGauge;

    private Integer chaosCheck;

    private Integer guardianGauge;

    private Integer guardianCheck;
}
