package lostark.todo.domain.inspection.service;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.character.dto.CharacterJsonDto;
import lostark.todo.domain.inspection.dto.*;
import lostark.todo.domain.inspection.entity.*;
import lostark.todo.domain.inspection.util.EquipmentChangeDetector;
import lostark.todo.domain.inspection.util.EquipmentParsingUtil;
import lostark.todo.domain.inspection.repository.CombatPowerHistoryRepository;
import lostark.todo.domain.inspection.repository.InspectionCharacterRepository;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.domain.notification.service.NotificationService;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class InspectionService {

    private final InspectionCharacterRepository inspectionCharacterRepository;
    private final CombatPowerHistoryRepository combatPowerHistoryRepository;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final NotificationService notificationService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;
    private final ExecutorService inspectionExecutor;

    private static final long API_TIMEOUT_SECONDS = 10;

    public InspectionService(
            InspectionCharacterRepository inspectionCharacterRepository,
            CombatPowerHistoryRepository combatPowerHistoryRepository,
            LostarkCharacterApiClient lostarkCharacterApiClient,
            NotificationService notificationService,
            MemberService memberService,
            ObjectMapper objectMapper,
            @Qualifier("inspectionExecutor") ExecutorService inspectionExecutor) {
        this.inspectionCharacterRepository = inspectionCharacterRepository;
        this.combatPowerHistoryRepository = combatPowerHistoryRepository;
        this.lostarkCharacterApiClient = lostarkCharacterApiClient;
        this.notificationService = notificationService;
        this.memberService = memberService;
        this.objectMapper = objectMapper;
        this.inspectionExecutor = inspectionExecutor;
    }

    /**
     * 군장검사 캐릭터 등록
     */
    public InspectionCharacterResponse create(String username, CreateInspectionCharacterRequest request) {
        Member member = memberService.get(username);

        if (member.getApiKey() == null || member.getApiKey().isEmpty()) {
            throw new ConditionNotMetException("API 키가 등록되어 있지 않습니다. 마이페이지에서 API 키를 등록해주세요.");
        }

        // 중복 체크
        boolean isDuplicate = inspectionCharacterRepository.existsByMemberAndCharacterName(
                member, request.getCharacterName());
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
                .title(profile.getTitle())
                .guildName(profile.getGuildName())
                .townName(profile.getTownName())
                .townLevel(profile.getTownLevel())
                .expeditionLevel(profile.getExpeditionLevel())
                .noChangeThreshold(request.getNoChangeThreshold())
                .isActive(true)
                .histories(new ArrayList<>())
                .build();

        inspectionCharacterRepository.save(inspectionCharacter);

        // 초기 히스토리 저장 (모든 API 데이터 병렬 조회)
        String charName = request.getCharacterName();
        String key = member.getApiKey();
        try {
            CompletableFuture<List<ArkgridEffectDto>> effectsFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getArkgridEffects(charName, key), inspectionExecutor);
            CompletableFuture<List<EquipmentDto>> equipmentFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getEquipment(charName, key), inspectionExecutor);
            CompletableFuture<List<EngravingDto>> engravingsFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getEngravings(charName, key), inspectionExecutor);
            CompletableFuture<CardApiResponse> cardsFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getCards(charName, key), inspectionExecutor);
            CompletableFuture<List<GemDto>> gemsFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getGems(charName, key), inspectionExecutor);
            CompletableFuture<ArkPassiveApiResponse> arkPassiveFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getArkPassive(charName, key), inspectionExecutor);

            saveHistoryRecord(inspectionCharacter, profile,
                    effectsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                    equipmentFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                    engravingsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                    cardsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                    gemsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                    arkPassiveFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.warn("초기 히스토리 저장 실패 - 캐릭터: {}, 오류: {}", charName, e.getMessage());
        }

        return InspectionCharacterResponse.from(inspectionCharacter);
    }

    /**
     * 군장검사 캐릭터 목록 조회
     */
    @Transactional(readOnly = true)
    public List<InspectionCharacterResponse> getAll(String username) {
        Member member = memberService.get(username);
        List<InspectionCharacter> characters = inspectionCharacterRepository.findByMember(member);

        if (characters.isEmpty()) {
            return Collections.emptyList();
        }

        // 배치 쿼리로 모든 캐릭터의 변화량 정보를 한 번에 조회
        List<Long> characterIds = characters.stream()
                .map(InspectionCharacter::getId)
                .collect(Collectors.toList());

        Map<Long, List<CombatPowerHistory>> latest2Map =
                combatPowerHistoryRepository.findLatest2ByCharacterIds(characterIds);
        Map<Long, Long> unchangedDaysMap =
                combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(characterIds);

        return characters.stream().map(character -> {
            InspectionCharacterResponse response = InspectionCharacterResponse.from(character);

            // 배치 조회 결과로 변화량 정보 설정
            List<CombatPowerHistory> latest2 = latest2Map.getOrDefault(character.getId(), Collections.emptyList());
            if (latest2.size() >= 2) {
                CombatPowerHistory latest = latest2.get(0);
                CombatPowerHistory previous = latest2.get(1);
                response.setPreviousCombatPower(previous.getCombatPower());
                response.setCombatPowerChange(latest.getCombatPower() - previous.getCombatPower());
                response.setPreviousItemLevel(previous.getItemLevel());
                response.setItemLevelChange(latest.getItemLevel() - previous.getItemLevel());
            }
            response.setUnchangedDays(unchangedDaysMap.getOrDefault(character.getId(), 0L));

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
     * API 호출은 트랜잭션 밖에서 수행하고, DB 저장만 트랜잭션으로 처리
     */
    public void fetchDailyData(InspectionCharacter character, String apiKey) {
        try {
            String charName = character.getCharacterName();

            // 1. 모든 API를 병렬로 조회 (트랜잭션 밖에서 수행)
            CompletableFuture<CharacterJsonDto> profileFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getCharacterProfileForInspection(charName, apiKey), inspectionExecutor);
            CompletableFuture<List<ArkgridEffectDto>> effectsFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getArkgridEffects(charName, apiKey), inspectionExecutor);
            CompletableFuture<List<EquipmentDto>> equipmentFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getEquipment(charName, apiKey), inspectionExecutor);
            CompletableFuture<List<EngravingDto>> engravingsFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getEngravings(charName, apiKey), inspectionExecutor);
            CompletableFuture<CardApiResponse> cardsFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getCards(charName, apiKey), inspectionExecutor);
            CompletableFuture<List<GemDto>> gemsFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getGems(charName, apiKey), inspectionExecutor);
            CompletableFuture<ArkPassiveApiResponse> arkPassiveFuture = CompletableFuture.supplyAsync(() ->
                    lostarkCharacterApiClient.getArkPassive(charName, apiKey), inspectionExecutor);

            CharacterJsonDto profile = profileFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            List<ArkgridEffectDto> effects = effectsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            List<EquipmentDto> equipments = equipmentFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            List<EngravingDto> engravings = engravingsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            CardApiResponse cardsResponse = cardsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            List<GemDto> gems = gemsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            ArkPassiveApiResponse arkPassiveResponse = arkPassiveFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            // 2. DB 저장은 별도 트랜잭션으로 처리
            saveFetchedData(character, profile, effects, equipments,
                    engravings, cardsResponse, gems, arkPassiveResponse);

        } catch (TimeoutException e) {
            log.error("군장검사 API 타임아웃 - 캐릭터: {}", character.getCharacterName());
        } catch (Exception e) {
            log.error("군장검사 데이터 수집 실패 - 캐릭터: {}, 오류: {}",
                    character.getCharacterName(), e.getMessage());
        }
    }

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
    private void saveHistoryRecord(InspectionCharacter character, CharacterJsonDto profile,
                                   List<ArkgridEffectDto> effects, List<EquipmentDto> equipments,
                                   List<EngravingDto> engravings, CardApiResponse cardsResponse,
                                   List<GemDto> gems, ArkPassiveApiResponse arkPassiveResponse) {
        LocalDate today = LocalDate.now();
        String statsJson = serializeStats(profile.getStats());

        // 오늘 기록이 이미 있으면 업데이트, 없으면 새로 생성
        Optional<CombatPowerHistory> existingHistory = combatPowerHistoryRepository
                .findByCharacterAndDate(character.getId(), today);

        CombatPowerHistory history;
        if (existingHistory.isPresent()) {
            history = existingHistory.get();
            history.updateData(profile.getCombatPower(), profile.getItemAvgLevel(), profile.getCharacterImage(), statsJson);
        } else {
            history = CombatPowerHistory.builder()
                    .inspectionCharacter(character)
                    .recordDate(today)
                    .combatPower(profile.getCombatPower())
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
                        .option(g.getOption())
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
     * 이전 히스토리의 장비 목록 조회
     */
    private List<EquipmentHistory> getPreviousEquipments(long inspectionCharacterId) {
        return combatPowerHistoryRepository.findLatest(inspectionCharacterId)
                .map(CombatPowerHistory::getEquipments)
                .orElse(Collections.emptyList());
    }

    /**
     * 장비 변화 감지 후 알림 발송
     */
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

    /**
     * 수집 시간 조회
     */
    @Transactional(readOnly = true)
    public int getScheduleHour(String username) {
        Member member = memberService.get(username);
        return member.getInspectionScheduleHour();
    }

    /**
     * 수집 시간 변경
     */
    public void updateScheduleHour(String username, int scheduleHour) {
        Member member = memberService.get(username);
        member.setInspectionScheduleHour(scheduleHour);
    }

    /**
     * Stats 배열을 JSON 문자열로 직렬화
     */
    private String serializeStats(List<CharacterJsonDto.StatDto> stats) {
        if (stats == null || stats.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(stats);
        } catch (Exception e) {
            log.warn("스탯 직렬화 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 변화량 정보 추가
     */
    private void enrichWithChangeInfo(InspectionCharacterResponse response, long inspectionCharacterId) {
        // 최근 2개 기록만 조회 (limit 2)
        List<CombatPowerHistory> latest2 = combatPowerHistoryRepository
                .findLatest2(inspectionCharacterId);

        if (latest2.size() >= 2) {
            CombatPowerHistory latest = latest2.get(0);   // desc 정렬이므로 첫 번째가 최신
            CombatPowerHistory previous = latest2.get(1);
            response.setPreviousCombatPower(previous.getCombatPower());
            response.setCombatPowerChange(latest.getCombatPower() - previous.getCombatPower());
            response.setPreviousItemLevel(previous.getItemLevel());
            response.setItemLevelChange(latest.getItemLevel() - previous.getItemLevel());
        }

        long unchangedDays = combatPowerHistoryRepository.countConsecutiveUnchangedDays(inspectionCharacterId);
        response.setUnchangedDays(unchangedDays);
    }
}
