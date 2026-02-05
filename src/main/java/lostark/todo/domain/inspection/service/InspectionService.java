package lostark.todo.domain.inspection.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterJsonDto;
import lostark.todo.domain.inspection.dto.*;
import lostark.todo.domain.inspection.entity.ArkgridEffectHistory;
import lostark.todo.domain.inspection.entity.CombatPowerHistory;
import lostark.todo.domain.inspection.entity.InspectionCharacter;
import lostark.todo.domain.inspection.repository.CombatPowerHistoryRepository;
import lostark.todo.domain.inspection.repository.InspectionCharacterRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.notification.service.NotificationService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InspectionService {

    private final InspectionCharacterRepository inspectionCharacterRepository;
    private final CombatPowerHistoryRepository combatPowerHistoryRepository;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final NotificationService notificationService;
    private final MemberService memberService;

    /**
     * 군장검사 캐릭터 등록
     */
    public InspectionCharacterResponse create(String username, CreateInspectionCharacterRequest request) {
        Member member = memberService.get(username);

        if (member.getApiKey() == null || member.getApiKey().isEmpty()) {
            throw new ConditionNotMetException("API 키가 등록되어 있지 않습니다. 마이페이지에서 API 키를 등록해주세요.");
        }

        // 중복 체크
        Optional<InspectionCharacter> existing = inspectionCharacterRepository
                .findByIdAndUsername(0, username); // dummy - check by name instead
        List<InspectionCharacter> memberChars = inspectionCharacterRepository.findByMember(member);
        boolean isDuplicate = memberChars.stream()
                .anyMatch(c -> c.getCharacterName().equals(request.getCharacterName()));
        if (isDuplicate) {
            throw new ConditionNotMetException("이미 등록된 캐릭터입니다: " + request.getCharacterName());
        }

        // Lostark API로 캐릭터 정보 가져오기
        CharacterJsonDto profile = lostarkCharacterApiClient
                .getCharacterProfileForInspection(request.getCharacterName(), member.getApiKey());

        InspectionCharacter inspectionCharacter = InspectionCharacter.builder()
                .member(member)
                .characterName(profile.getCharacterName())
                .serverName(profile.getServerName())
                .characterClassName(profile.getCharacterClassName())
                .characterImage(profile.getCharacterImage())
                .itemLevel(profile.getItemAvgLevel())
                .combatPower(profile.getCombatPower())
                .noChangeThreshold(request.getNoChangeThreshold())
                .isActive(true)
                .histories(new ArrayList<>())
                .build();

        inspectionCharacterRepository.save(inspectionCharacter);

        // 초기 히스토리 저장
        saveHistoryRecord(inspectionCharacter, profile, member.getApiKey());

        return InspectionCharacterResponse.from(inspectionCharacter);
    }

    /**
     * 군장검사 캐릭터 목록 조회
     */
    @Transactional(readOnly = true)
    public List<InspectionCharacterResponse> getAll(String username) {
        Member member = memberService.get(username);
        List<InspectionCharacter> characters = inspectionCharacterRepository.findByMember(member);

        return characters.stream().map(character -> {
            InspectionCharacterResponse response = InspectionCharacterResponse.from(character);

            // 변화량 정보 추가
            enrichWithChangeInfo(response, character.getId());

            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 군장검사 캐릭터 상세 조회 (차트 데이터 포함)
     */
    @Transactional(readOnly = true)
    public InspectionDashboardResponse getDetail(String username, long inspectionCharacterId,
                                                  LocalDate startDate, LocalDate endDate) {
        InspectionCharacter character = inspectionCharacterRepository
                .findByIdAndUsername(inspectionCharacterId, username)
                .orElseThrow(() -> new ConditionNotMetException("캐릭터를 찾을 수 없습니다."));

        InspectionCharacterResponse characterResponse = InspectionCharacterResponse.from(character);
        enrichWithChangeInfo(characterResponse, character.getId());

        List<CombatPowerHistory> histories = combatPowerHistoryRepository
                .findByCharacterAndDateRange(inspectionCharacterId, startDate, endDate);

        List<CombatPowerHistoryResponse> historyResponses = histories.stream()
                .map(CombatPowerHistoryResponse::from)
                .collect(Collectors.toList());

        return InspectionDashboardResponse.builder()
                .character(characterResponse)
                .histories(historyResponses)
                .build();
    }

    /**
     * 군장검사 캐릭터 설정 수정
     */
    public void update(String username, long inspectionCharacterId, UpdateInspectionCharacterRequest request) {
        InspectionCharacter character = inspectionCharacterRepository
                .findByIdAndUsername(inspectionCharacterId, username)
                .orElseThrow(() -> new ConditionNotMetException("캐릭터를 찾을 수 없습니다."));

        character.updateSettings(request.getNoChangeThreshold(), request.isActive());
    }

    /**
     * 군장검사 캐릭터 삭제
     */
    public void delete(String username, long inspectionCharacterId) {
        InspectionCharacter character = inspectionCharacterRepository
                .findByIdAndUsername(inspectionCharacterId, username)
                .orElseThrow(() -> new ConditionNotMetException("캐릭터를 찾을 수 없습니다."));

        inspectionCharacterRepository.delete(character);
    }

    /**
     * 수동 새로고침
     */
    public InspectionDashboardResponse refresh(String username, long inspectionCharacterId) {
        Member member = memberService.get(username);

        if (member.getApiKey() == null || member.getApiKey().isEmpty()) {
            throw new ConditionNotMetException("API 키가 등록되어 있지 않습니다.");
        }

        InspectionCharacter character = inspectionCharacterRepository
                .findByIdAndUsername(inspectionCharacterId, username)
                .orElseThrow(() -> new ConditionNotMetException("캐릭터를 찾을 수 없습니다."));

        fetchDailyData(character, member.getApiKey());

        // 상세 데이터 반환 (최근 30일)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        return getDetail(username, inspectionCharacterId, startDate, endDate);
    }

    /**
     * 일일 데이터 수집 (스케줄러 및 수동 새로고침에서 호출)
     */
    public void fetchDailyData(InspectionCharacter character, String apiKey) {
        try {
            // 1. 프로필 조회
            CharacterJsonDto profile = lostarkCharacterApiClient
                    .getCharacterProfileForInspection(character.getCharacterName(), apiKey);

            // 2. 이전 전투력 저장
            double previousCombatPower = character.getCombatPower();

            // 3. 캐릭터 정보 업데이트
            character.updateProfile(
                    profile.getCharacterImage(),
                    profile.getItemAvgLevel(),
                    profile.getCombatPower(),
                    profile.getServerName(),
                    profile.getCharacterClassName()
            );

            // 4. 히스토리 저장
            saveHistoryRecord(character, profile, apiKey);

            // 5. 알림 체크
            checkAndNotify(character, profile.getCombatPower(), previousCombatPower);

        } catch (Exception e) {
            log.error("군장검사 데이터 수집 실패 - 캐릭터: {}, 오류: {}",
                    character.getCharacterName(), e.getMessage());
        }
    }

    /**
     * 히스토리 레코드 저장 (upsert)
     */
    private void saveHistoryRecord(InspectionCharacter character, CharacterJsonDto profile, String apiKey) {
        LocalDate today = LocalDate.now();

        // 아크그리드 효과 조회
        List<ArkgridEffectDto> effects = lostarkCharacterApiClient
                .getArkgridEffects(character.getCharacterName(), apiKey);

        // 오늘 기록이 이미 있으면 업데이트, 없으면 새로 생성
        Optional<CombatPowerHistory> existingHistory = combatPowerHistoryRepository
                .findByCharacterAndDate(character.getId(), today);

        CombatPowerHistory history;
        if (existingHistory.isPresent()) {
            history = existingHistory.get();
            history.updateData(profile.getCombatPower(), profile.getItemAvgLevel(), profile.getCharacterImage());
        } else {
            history = CombatPowerHistory.builder()
                    .inspectionCharacter(character)
                    .recordDate(today)
                    .combatPower(profile.getCombatPower())
                    .itemLevel(profile.getItemAvgLevel())
                    .characterImage(profile.getCharacterImage())
                    .arkgridEffects(new ArrayList<>())
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
    }

    /**
     * 전투력 변화 알림 체크
     */
    private void checkAndNotify(InspectionCharacter character, double newCombatPower, double previousCombatPower) {
        Member member = character.getMember();

        // 전투력 증가 알림
        if (Double.compare(newCombatPower, previousCombatPower) > 0 && previousCombatPower > 0) {
            String content = String.format("[%s] 전투력이 증가했습니다! (%.2f → %.2f)",
                    character.getCharacterName(), previousCombatPower, newCombatPower);
            notificationService.createInspectionNotification(member, content, character.getId());
            log.info("전투력 증가 알림 생성 - 캐릭터: {}, {} → {}",
                    character.getCharacterName(), previousCombatPower, newCombatPower);
        }

        // N일 무변동 알림
        long unchangedDays = combatPowerHistoryRepository.countConsecutiveUnchangedDays(character.getId());
        if (unchangedDays >= character.getNoChangeThreshold()) {
            String content = String.format("[%s] 전투력이 %d일 동안 변동이 없습니다.",
                    character.getCharacterName(), unchangedDays);
            notificationService.createInspectionNotification(member, content, character.getId());
            log.info("무변동 알림 생성 - 캐릭터: {}, {}일 무변동",
                    character.getCharacterName(), unchangedDays);
        }
    }

    /**
     * 변화량 정보 추가
     */
    private void enrichWithChangeInfo(InspectionCharacterResponse response, long inspectionCharacterId) {
        // 최근 2개 기록 가져오기
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<CombatPowerHistory> recentHistories = combatPowerHistoryRepository
                .findByCharacterAndDateRange(inspectionCharacterId, startDate, endDate);

        if (recentHistories.size() >= 2) {
            CombatPowerHistory latest = recentHistories.get(recentHistories.size() - 1);
            CombatPowerHistory previous = recentHistories.get(recentHistories.size() - 2);
            response.setPreviousCombatPower(previous.getCombatPower());
            response.setCombatPowerChange(latest.getCombatPower() - previous.getCombatPower());
        }

        long unchangedDays = combatPowerHistoryRepository.countConsecutiveUnchangedDays(inspectionCharacterId);
        response.setUnchangedDays(unchangedDays);
    }
}
