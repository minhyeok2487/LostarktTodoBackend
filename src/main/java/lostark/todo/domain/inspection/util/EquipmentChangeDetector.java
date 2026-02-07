package lostark.todo.domain.inspection.util;

import lostark.todo.domain.inspection.entity.EquipmentHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EquipmentChangeDetector {

    private static final int MAX_NOTIFICATIONS_PER_CHARACTER = 5;

    private static final Set<String> EXCLUDED_TYPES = Set.of("나침반", "부적", "보주");

    private EquipmentChangeDetector() {
    }

    /**
     * 이전 장비 목록과 새 장비 목록을 비교하여 변화 메시지를 생성한다.
     * 한 캐릭터당 최대 MAX_NOTIFICATIONS_PER_CHARACTER개의 메시지만 반환한다.
     */
    public static List<String> detectChanges(String characterName,
                                              List<EquipmentHistory> previousEquipments,
                                              List<EquipmentHistory> newEquipments) {
        if (previousEquipments == null || previousEquipments.isEmpty()) {
            return List.of();
        }
        if (newEquipments == null || newEquipments.isEmpty()) {
            return List.of();
        }

        Map<String, EquipmentHistory> prevByType = previousEquipments.stream()
                .collect(Collectors.toMap(EquipmentHistory::getType, e -> e, (a, b) -> a));

        List<String> changes = new ArrayList<>();

        for (EquipmentHistory newEquip : newEquipments) {
            if (changes.size() >= MAX_NOTIFICATIONS_PER_CHARACTER) {
                break;
            }

            // 비전투 장비는 알림 제외
            if (EXCLUDED_TYPES.contains(newEquip.getType())) {
                continue;
            }

            EquipmentHistory prevEquip = prevByType.get(newEquip.getType());
            if (prevEquip == null) {
                continue;
            }

            // 장비 교체 감지 (같은 슬롯에 다른 이름)
            if (!Objects.equals(prevEquip.getName(), newEquip.getName())) {
                changes.add(String.format("[%s] %s가 교체되었습니다!", characterName, newEquip.getType()));
                continue;
            }

            // 재련 단계 변화
            if (hasChanged(prevEquip.getRefinement(), newEquip.getRefinement())) {
                changes.add(String.format("[%s] %s가 강화되었습니다! (+%d → +%d)",
                        characterName, newEquip.getType(),
                        prevEquip.getRefinement(), newEquip.getRefinement()));
            }

            // 상급 재련 변화
            if (changes.size() < MAX_NOTIFICATIONS_PER_CHARACTER
                    && hasChanged(prevEquip.getAdvancedRefinement(), newEquip.getAdvancedRefinement())) {
                int prevAdv = prevEquip.getAdvancedRefinement() != null ? prevEquip.getAdvancedRefinement() : 0;
                int newAdv = newEquip.getAdvancedRefinement() != null ? newEquip.getAdvancedRefinement() : 0;
                changes.add(String.format("[%s] %s 상급 재련이 올랐습니다! (%d단계 → %d단계)",
                        characterName, newEquip.getType(), prevAdv, newAdv));
            }

            // 품질 변화
            if (changes.size() < MAX_NOTIFICATIONS_PER_CHARACTER
                    && hasChanged(prevEquip.getQuality(), newEquip.getQuality())) {
                changes.add(String.format("[%s] %s 품질이 변경되었습니다! (%d → %d)",
                        characterName, newEquip.getType(),
                        prevEquip.getQuality(), newEquip.getQuality()));
            }
        }

        return changes;
    }

    private static boolean hasChanged(Integer prev, Integer next) {
        return !Objects.equals(prev, next);
    }
}
