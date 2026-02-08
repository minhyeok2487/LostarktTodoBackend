package lostark.todo.domain.inspection.util;

import lostark.todo.domain.inspection.entity.EquipmentHistory;

import java.util.*;

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

        // 인덱스 기반 매칭: 동일 이름/타입 중복 장비(귀걸이x2, 반지x2) 처리
        Set<Integer> matchedPrevIndices = new HashSet<>();

        List<String> changes = new ArrayList<>();

        for (EquipmentHistory newEquip : newEquipments) {
            if (changes.size() >= MAX_NOTIFICATIONS_PER_CHARACTER) {
                break;
            }

            // 비전투 장비는 알림 제외
            if (EXCLUDED_TYPES.contains(newEquip.getType())) {
                continue;
            }

            // 1차: 이름이 같은 이전 장비 중 아직 매칭 안 된 것
            EquipmentHistory prevEquip = null;
            for (int i = 0; i < previousEquipments.size(); i++) {
                if (!matchedPrevIndices.contains(i)
                        && Objects.equals(previousEquipments.get(i).getName(), newEquip.getName())) {
                    prevEquip = previousEquipments.get(i);
                    matchedPrevIndices.add(i);
                    break;
                }
            }
            // 2차: 같은 타입의 이전 장비 중 아직 매칭 안 된 것
            if (prevEquip == null) {
                for (int i = 0; i < previousEquipments.size(); i++) {
                    if (!matchedPrevIndices.contains(i)
                            && Objects.equals(previousEquipments.get(i).getType(), newEquip.getType())) {
                        prevEquip = previousEquipments.get(i);
                        matchedPrevIndices.add(i);
                        break;
                    }
                }
            }
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
                int prevRef = prevEquip.getRefinement() != null ? prevEquip.getRefinement() : 0;
                int newRef = newEquip.getRefinement() != null ? newEquip.getRefinement() : 0;
                changes.add(String.format("[%s] %s가 강화되었습니다! (+%d → +%d)",
                        characterName, newEquip.getType(), prevRef, newRef));
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
                int prevQual = prevEquip.getQuality() != null ? prevEquip.getQuality() : 0;
                int newQual = newEquip.getQuality() != null ? newEquip.getQuality() : 0;
                changes.add(String.format("[%s] %s 품질이 변경되었습니다! (%d → %d)",
                        characterName, newEquip.getType(), prevQual, newQual));
            }

            // 연마 효과 변화
            if (changes.size() < MAX_NOTIFICATIONS_PER_CHARACTER
                    && hasStringChanged(prevEquip.getGrindingEffect(), newEquip.getGrindingEffect())) {
                changes.add(String.format("[%s] %s 연마 효과가 변경되었습니다!",
                        characterName, newEquip.getType()));
            }

            // 아크 패시브 포인트 변화
            if (changes.size() < MAX_NOTIFICATIONS_PER_CHARACTER
                    && hasStringChanged(prevEquip.getArkPassiveEffect(), newEquip.getArkPassiveEffect())) {
                String prevArk = prevEquip.getArkPassiveEffect() != null ? prevEquip.getArkPassiveEffect() : "없음";
                String newArk = newEquip.getArkPassiveEffect() != null ? newEquip.getArkPassiveEffect() : "없음";
                changes.add(String.format("[%s] %s 아크 패시브가 변경되었습니다! (%s → %s)",
                        characterName, newEquip.getType(), prevArk, newArk));
            }

            // 팔찌 효과 변화
            if (changes.size() < MAX_NOTIFICATIONS_PER_CHARACTER
                    && hasStringChanged(prevEquip.getBraceletEffect(), newEquip.getBraceletEffect())) {
                changes.add(String.format("[%s] %s 효과가 변경되었습니다!",
                        characterName, newEquip.getType()));
            }
        }

        return changes;
    }

    private static boolean hasChanged(Integer prev, Integer next) {
        return !Objects.equals(prev, next);
    }

    private static boolean hasStringChanged(String prev, String next) {
        return !Objects.equals(prev, next);
    }
}
