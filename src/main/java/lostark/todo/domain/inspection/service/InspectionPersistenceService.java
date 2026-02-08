package lostark.todo.domain.inspection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterJsonDto;
import lostark.todo.domain.inspection.dto.*;
import lostark.todo.domain.inspection.entity.*;
import lostark.todo.domain.inspection.util.EquipmentChangeDetector;
import lostark.todo.domain.inspection.util.EquipmentParsingUtil;
import lostark.todo.domain.inspection.repository.CombatPowerHistoryRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 군장검사 데이터 영속화 전용 서비스
 * Self-invocation 프록시 문제를 방지하기 위해 InspectionService에서 분리
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InspectionPersistenceService {

    private final CombatPowerHistoryRepository combatPowerHistoryRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    /**
     * API에서 가져온 데이터를 DB에 저장 (개별 트랜잭션)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFetchedData(InspectionCharacter character, CharacterJsonDto profile,
                                 List<ArkgridEffectDto> effects, List<EquipmentDto> equipments,
                                 List<EngravingDto> engravings, CardApiResponse cardsResponse,
                                 List<GemDto> gems, ArkPassiveApiResponse arkPassiveResponse) {
        double previousCombatPower = character.getCombatPower();
        List<EquipmentHistory> previousEquipments = getPreviousEquipments(character.getId());

        character.updateProfile(
                profile.getCharacterImage(),
                profile.getItemAvgLevel(),
                profile.getCombatPower(),
                profile.getServerName(),
                profile.getCharacterClassName(),
                profile.getTitle(),
                profile.getGuildName(),
                profile.getTownName(),
                profile.getTownLevel(),
                profile.getExpeditionLevel()
        );

        saveHistoryRecord(character, profile, effects, equipments,
                engravings, cardsResponse, gems, arkPassiveResponse);

        checkAndNotify(character, profile.getCombatPower(), previousCombatPower);

        List<EquipmentHistory> newEquipments = equipments.stream()
                .map(EquipmentParsingUtil::parse)
                .collect(Collectors.toList());
        checkEquipmentChanges(character, previousEquipments, newEquipments);
    }

    /**
     * 히스토리 레코드 저장 (upsert) - 전체 API 데이터 포함
     */
    @Transactional
    public void saveHistoryRecord(InspectionCharacter character, CharacterJsonDto profile,
                                   List<ArkgridEffectDto> effects, List<EquipmentDto> equipments,
                                   List<EngravingDto> engravings, CardApiResponse cardsResponse,
                                   List<GemDto> gems, ArkPassiveApiResponse arkPassiveResponse) {
        LocalDate today = LocalDate.now();
        String statsJson = serializeStats(profile.getStats());

        Optional<CombatPowerHistory> existingHistory = combatPowerHistoryRepository
                .findByCharacterAndDate(character.getId(), today);

        CombatPowerHistory history;
        if (existingHistory.isPresent()) {
            history = existingHistory.get();
            history.updateData(character.getCombatPower(), profile.getItemAvgLevel(), profile.getCharacterImage(), statsJson);
        } else {
            history = CombatPowerHistory.builder()
                    .inspectionCharacter(character)
                    .recordDate(today)
                    .combatPower(character.getCombatPower())
                    .itemLevel(profile.getItemAvgLevel())
                    .characterImage(profile.getCharacterImage())
                    .statsJson(statsJson)
                    .arkgridEffects(new ArrayList<>())
                    .equipments(new ArrayList<>())
                    .engravings(new ArrayList<>())
                    .cards(new ArrayList<>())
                    .cardSetEffects(new ArrayList<>())
                    .gems(new ArrayList<>())
                    .arkPassivePoints(new ArrayList<>())
                    .arkPassiveEffects(new ArrayList<>())
                    .build();
            combatPowerHistoryRepository.save(history);
        }

        // 아크그리드 효과 저장
        List<ArkgridEffectHistory> effectHistories = effects.stream()
                .map(effect -> ArkgridEffectHistory.builder()
                        .effectName(effect.getName())
                        .effectLevel(effect.getLevel())
                        .effectTooltip(effect.getTooltip())
                        .build())
                .collect(Collectors.toList());
        history.replaceArkgridEffects(effectHistories);

        // 장비 정보 저장
        List<EquipmentHistory> equipmentHistories = equipments.stream()
                .map(EquipmentParsingUtil::parse)
                .collect(Collectors.toList());
        history.replaceEquipments(equipmentHistories);

        // 각인 정보 저장
        List<EngravingHistory> engravingHistories = engravings.stream()
                .map(e -> EngravingHistory.builder()
                        .name(e.getName())
                        .level(e.getLevel())
                        .grade(e.getGrade())
                        .abilityStoneLevel(e.getAbilityStoneLevel())
                        .description(e.getDescription())
                        .build())
                .collect(Collectors.toList());
        history.replaceEngravings(engravingHistories);

        // 카드 정보 저장
        List<CardHistory> cardHistories = cardsResponse.getCards().stream()
                .map(c -> CardHistory.builder()
                        .slot(c.getSlot())
                        .name(c.getName())
                        .icon(c.getIcon())
                        .awakeCount(c.getAwakeCount())
                        .awakeTotal(c.getAwakeTotal())
                        .grade(c.getGrade())
                        .build())
                .collect(Collectors.toList());
        history.replaceCards(cardHistories);

        // 카드 세트효과 저장
        List<CardSetEffectHistory> cardSetEffectHistories = cardsResponse.getCardSetEffects().stream()
                .map(e -> CardSetEffectHistory.builder()
                        .name(e.getName())
                        .description(e.getDescription())
                        .build())
                .collect(Collectors.toList());
        history.replaceCardSetEffects(cardSetEffectHistories);

        // 보석 정보 저장
        List<GemHistory> gemHistories = gems.stream()
                .map(g -> GemHistory.builder()
                        .skillName(g.getSkillName())
                        .gemSlot(g.getGemSlot())
                        .skillIcon(g.getSkillIcon())
                        .level(g.getLevel())
                        .grade(g.getGrade())
                        .description(g.getDescription())
                        .gemOption(g.getGemOption())
                        .build())
                .collect(Collectors.toList());
        history.replaceGems(gemHistories);

        // 아크패시브 정보 저장
        history.setArkPassiveTitle(arkPassiveResponse.getTitle());
        history.setTownName(profile.getTownName());
        history.setTownLevel(profile.getTownLevel());

        List<ArkPassivePointHistory> arkPassivePointHistories = arkPassiveResponse.getPoints().stream()
                .map(p -> ArkPassivePointHistory.builder()
                        .name(p.getName())
                        .value(p.getValue())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toList());
        history.replaceArkPassivePoints(arkPassivePointHistories);

        List<ArkPassiveEffectHistory> arkPassiveEffectHistories = arkPassiveResponse.getEffects().stream()
                .map(e -> ArkPassiveEffectHistory.builder()
                        .category(e.getCategory())
                        .effectName(e.getEffectName())
                        .icon(e.getIcon())
                        .tier(e.getTier())
                        .level(e.getLevel())
                        .build())
                .collect(Collectors.toList());
        history.replaceArkPassiveEffects(arkPassiveEffectHistories);
    }

    private void checkAndNotify(InspectionCharacter character, double newCombatPower, double previousCombatPower) {
        Member member = character.getMember();

        if (Double.compare(newCombatPower, previousCombatPower) > 0 && previousCombatPower > 0) {
            String content = String.format("[%s] 전투력이 증가했습니다! (%.2f → %.2f)",
                    character.getCharacterName(), previousCombatPower, newCombatPower);
            notificationService.createInspectionNotification(member, content, character.getId());
            log.info("전투력 증가 알림 생성 - 캐릭터: {}, {} → {}",
                    character.getCharacterName(), previousCombatPower, newCombatPower);
        }

        long unchangedDays = combatPowerHistoryRepository.countConsecutiveUnchangedDays(character.getId());
        if (unchangedDays >= character.getNoChangeThreshold()) {
            String content = String.format("[%s] 전투력이 %d일 동안 변동이 없습니다.",
                    character.getCharacterName(), unchangedDays);
            notificationService.createInspectionNotification(member, content, character.getId());
            log.info("무변동 알림 생성 - 캐릭터: {}, {}일 무변동",
                    character.getCharacterName(), unchangedDays);
        }
    }

    private List<EquipmentHistory> getPreviousEquipments(long inspectionCharacterId) {
        return combatPowerHistoryRepository.findLatest(inspectionCharacterId)
                .map(CombatPowerHistory::getEquipments)
                .orElse(Collections.emptyList());
    }

    private void checkEquipmentChanges(InspectionCharacter character,
                                        List<EquipmentHistory> previousEquipments,
                                        List<EquipmentHistory> newEquipments) {
        List<String> changes = EquipmentChangeDetector.detectChanges(
                character.getCharacterName(), previousEquipments, newEquipments);

        if (changes.isEmpty()) {
            return;
        }

        Member member = character.getMember();
        String content = String.join("\n", changes);
        notificationService.createInspectionNotification(member, content, character.getId());
        log.info("장비 변화 알림 생성 - 캐릭터: {}, 변화: {}건", character.getCharacterName(), changes.size());
    }

    private String serializeStats(List<CharacterJsonDto.StatDto> stats) {
        if (stats == null || stats.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(stats);
        } catch (Exception e) {
            log.warn("스탯 직렬화 실패", e);
            return null;
        }
    }
}
