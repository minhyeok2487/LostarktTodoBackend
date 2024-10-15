package lostark.todo.domainV2.character.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateDayGaugeRequest extends BaseCharacterRequest {

    private Integer eponaGauge;

    private Integer chaosGauge;

    private Integer guardianGauge;
}
