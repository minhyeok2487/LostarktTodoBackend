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
    private String townName;
    private Integer townLevel;
    private List<ArkgridEffectResponse> arkgridEffects;
    private List<EquipmentHistoryResponse> equipments;
    private List<EngravingHistoryResponse> engravings;
    private List<CardHistoryResponse> cards;
    private List<CardSetEffectHistoryResponse> cardSetEffects;
    private List<GemHistoryResponse> gems;
    private List<ArkPassivePointHistoryResponse> arkPassivePoints;
    private List<ArkPassiveEffectHistoryResponse> arkPassiveEffects;
    private String arkPassiveTitle;

    public static CombatPowerHistoryResponse from(CombatPowerHistory entity) {
        return CombatPowerHistoryResponse.builder()
                .id(entity.getId())
                .recordDate(entity.getRecordDate())
                .combatPower(entity.getCombatPower())
                .itemLevel(entity.getItemLevel())
                .statsJson(entity.getStatsJson())
                .townName(entity.getTownName())
                .townLevel(entity.getTownLevel())
                .arkgridEffects(entity.getArkgridEffects().stream()
                        .map(ArkgridEffectResponse::from)
                        .collect(Collectors.toList()))
                .equipments(entity.getEquipments().stream()
                        .map(EquipmentHistoryResponse::from)
                        .collect(Collectors.toList()))
                .engravings(entity.getEngravings().stream()
                        .map(EngravingHistoryResponse::from)
                        .collect(Collectors.toList()))
                .cards(entity.getCards().stream()
                        .map(CardHistoryResponse::from)
                        .collect(Collectors.toList()))
                .cardSetEffects(entity.getCardSetEffects().stream()
                        .map(CardSetEffectHistoryResponse::from)
                        .collect(Collectors.toList()))
                .gems(entity.getGems().stream()
                        .map(GemHistoryResponse::from)
                        .collect(Collectors.toList()))
                .arkPassivePoints(entity.getArkPassivePoints().stream()
                        .map(ArkPassivePointHistoryResponse::from)
                        .collect(Collectors.toList()))
                .arkPassiveEffects(entity.getArkPassiveEffects().stream()
                        .map(ArkPassiveEffectHistoryResponse::from)
                        .collect(Collectors.toList()))
                .arkPassiveTitle(entity.getArkPassiveTitle())
                .build();
    }
}
