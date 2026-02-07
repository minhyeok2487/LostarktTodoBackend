package lostark.todo.domain.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lostark.todo.domain.inspection.entity.CombatPowerHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombatPowerHistoryResponse {

    private long id;
    private LocalDate recordDate;
    private double combatPower;
    private double itemLevel;
    private String statsJson;
    private List<ArkgridEffectResponse> arkgridEffects;
    private List<EquipmentHistoryResponse> equipments;
    private List<EngravingHistoryResponse> engravings;

    public static CombatPowerHistoryResponse from(CombatPowerHistory entity) {
        return CombatPowerHistoryResponse.builder()
                .id(entity.getId())
                .recordDate(entity.getRecordDate())
                .combatPower(entity.getCombatPower())
                .itemLevel(entity.getItemLevel())
                .statsJson(entity.getStatsJson())
                .arkgridEffects(entity.getArkgridEffects().stream()
                        .map(ArkgridEffectResponse::from)
                        .collect(Collectors.toList()))
                .equipments(entity.getEquipments().stream()
                        .map(EquipmentHistoryResponse::from)
                        .collect(Collectors.toList()))
                .engravings(entity.getEngravings().stream()
                        .map(EngravingHistoryResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
