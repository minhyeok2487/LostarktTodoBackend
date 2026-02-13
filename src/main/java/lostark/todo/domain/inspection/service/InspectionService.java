// package lostark.todo.domain.inspection.service;
// 
// import lombok.extern.slf4j.Slf4j;
// import lostark.todo.domain.character.dto.CharacterJsonDto;
// import lostark.todo.domain.inspection.dto.*;
// import lostark.todo.domain.inspection.entity.*;
// import lostark.todo.domain.inspection.repository.CombatPowerHistoryRepository;
// import lostark.todo.domain.inspection.repository.InspectionCharacterRepository;
// import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
// import lostark.todo.domain.member.entity.Member;
// import lostark.todo.domain.member.service.MemberService;
// import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// 
// import java.time.LocalDate;
// import java.util.*;
// import java.util.concurrent.*;
// import java.util.stream.Collectors;
// 
// @Service
// @Transactional
// @Slf4j
// public class InspectionService {
// 
//     private final InspectionCharacterRepository inspectionCharacterRepository;
//     private final CombatPowerHistoryRepository combatPowerHistoryRepository;
//     private final LostarkCharacterApiClient lostarkCharacterApiClient;
//     private final InspectionPersistenceService inspectionPersistenceService;
//     private final MemberService memberService;
//     private final ExecutorService inspectionExecutor;
// 
//     private static final long API_TIMEOUT_SECONDS = 10;
// 
//     public InspectionService(
//             InspectionCharacterRepository inspectionCharacterRepository,
//             CombatPowerHistoryRepository combatPowerHistoryRepository,
//             LostarkCharacterApiClient lostarkCharacterApiClient,
//             InspectionPersistenceService inspectionPersistenceService,
//             MemberService memberService,
//             @Qualifier("inspectionExecutor") ExecutorService inspectionExecutor) {
//         this.inspectionCharacterRepository = inspectionCharacterRepository;
//         this.combatPowerHistoryRepository = combatPowerHistoryRepository;
//         this.lostarkCharacterApiClient = lostarkCharacterApiClient;
//         this.inspectionPersistenceService = inspectionPersistenceService;
//         this.memberService = memberService;
//         this.inspectionExecutor = inspectionExecutor;
//     }
// 
//     /**
//      * 군장검사 캐릭터 등록
//      */
//     public InspectionCharacterResponse create(String username, CreateInspectionCharacterRequest request) {
//         Member member = memberService.get(username);
// 
//         if (member.getApiKey() == null || member.getApiKey().isEmpty()) {
//             throw new ConditionNotMetException("API 키가 등록되어 있지 않습니다. 마이페이지에서 API 키를 등록해주세요.");
//         }
// 
//         // 중복 체크
//         boolean isDuplicate = inspectionCharacterRepository.existsByMemberAndCharacterName(
//                 member, request.getCharacterName());
//         if (isDuplicate) {
//             throw new ConditionNotMetException("이미 등록된 캐릭터입니다: " + request.getCharacterName());
//         }
// 
//         // Lostark API로 캐릭터 정보 가져오기
//         CharacterJsonDto profile = lostarkCharacterApiClient
//                 .getCharacterProfileForInspection(request.getCharacterName(), member.getApiKey());
// 
//         InspectionCharacter inspectionCharacter = InspectionCharacter.builder()
//                 .member(member)
//                 .characterName(profile.getCharacterName())
//                 .serverName(profile.getServerName())
//                 .characterClassName(profile.getCharacterClassName())
//                 .characterImage(profile.getCharacterImage())
//                 .itemLevel(profile.getItemAvgLevel())
//                 .combatPower(profile.getCombatPower())
//                 .title(profile.getTitle())
//                 .guildName(profile.getGuildName())
//                 .townName(profile.getTownName())
//                 .townLevel(profile.getTownLevel())
//                 .expeditionLevel(profile.getExpeditionLevel())
//                 .noChangeThreshold(request.getNoChangeThreshold())
//                 .isActive(true)
//                 .histories(new ArrayList<>())
//                 .build();
// 
//         inspectionCharacterRepository.save(inspectionCharacter);
// 
//         // 초기 히스토리 저장 (모든 API 데이터 병렬 조회)
//         String charName = request.getCharacterName();
//         String key = member.getApiKey();
//         try {
//             CompletableFuture<List<ArkgridEffectDto>> effectsFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getArkgridEffects(charName, key), inspectionExecutor);
//             CompletableFuture<List<EquipmentDto>> equipmentFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getEquipment(charName, key), inspectionExecutor);
//             CompletableFuture<List<EngravingDto>> engravingsFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getEngravings(charName, key), inspectionExecutor);
//             CompletableFuture<CardApiResponse> cardsFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getCards(charName, key), inspectionExecutor);
//             CompletableFuture<List<GemDto>> gemsFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getGems(charName, key), inspectionExecutor);
//             CompletableFuture<ArkPassiveApiResponse> arkPassiveFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getArkPassive(charName, key), inspectionExecutor);
// 
//             inspectionPersistenceService.saveHistoryRecord(inspectionCharacter, profile,
//                     effectsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
//                     equipmentFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
//                     engravingsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
//                     cardsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
//                     gemsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS),
//                     arkPassiveFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS));
//         } catch (Exception e) {
//             log.warn("초기 히스토리 저장 실패 - 캐릭터: {}", charName, e);
//         }
// 
//         return InspectionCharacterResponse.from(inspectionCharacter);
//     }
// 
//     /**
//      * 군장검사 캐릭터 목록 조회
//      */
//     @Transactional(readOnly = true)
//     public List<InspectionCharacterResponse> getAll(String username) {
//         Member member = memberService.get(username);
//         List<InspectionCharacter> characters = inspectionCharacterRepository.findByMember(member);
// 
//         if (characters.isEmpty()) {
//             return Collections.emptyList();
//         }
// 
//         // 배치 쿼리로 모든 캐릭터의 변화량 정보를 한 번에 조회
//         List<Long> characterIds = characters.stream()
//                 .map(InspectionCharacter::getId)
//                 .collect(Collectors.toList());
// 
//         Map<Long, List<CombatPowerHistory>> latest2Map =
//                 combatPowerHistoryRepository.findLatest2ByCharacterIds(characterIds);
//         Map<Long, Long> unchangedDaysMap =
//                 combatPowerHistoryRepository.countConsecutiveUnchangedDaysBatch(characterIds);
// 
//         return characters.stream().map(character -> {
//             InspectionCharacterResponse response = InspectionCharacterResponse.from(character);
// 
//             // 배치 조회 결과로 변화량 정보 설정
//             List<CombatPowerHistory> latest2 = latest2Map.getOrDefault(character.getId(), Collections.emptyList());
//             if (latest2.size() >= 2) {
//                 CombatPowerHistory latest = latest2.get(0);
//                 CombatPowerHistory previous = latest2.get(1);
//                 response.setPreviousCombatPower(previous.getCombatPower());
//                 response.setCombatPowerChange(latest.getCombatPower() - previous.getCombatPower());
//                 response.setPreviousItemLevel(previous.getItemLevel());
//                 response.setItemLevelChange(latest.getItemLevel() - previous.getItemLevel());
//             }
//             response.setUnchangedDays(unchangedDaysMap.getOrDefault(character.getId(), 0L));
// 
//             return response;
//         }).collect(Collectors.toList());
//     }
// 
//     /**
//      * 군장검사 캐릭터 상세 조회 (차트 데이터 포함)
//      */
//     @Transactional(readOnly = true)
//     public InspectionDashboardResponse getDetail(String username, long inspectionCharacterId,
//                                                   LocalDate startDate, LocalDate endDate) {
//         InspectionCharacter character = inspectionCharacterRepository
//                 .findByIdAndUsername(inspectionCharacterId, username)
//                 .orElseThrow(() -> new ConditionNotMetException("캐릭터를 찾을 수 없습니다."));
// 
//         InspectionCharacterResponse characterResponse = InspectionCharacterResponse.from(character);
//         enrichWithChangeInfo(characterResponse, character.getId());
// 
//         List<CombatPowerHistory> histories = combatPowerHistoryRepository
//                 .findByCharacterAndDateRange(inspectionCharacterId, startDate, endDate);
// 
//         List<CombatPowerHistoryResponse> historyResponses = histories.stream()
//                 .map(CombatPowerHistoryResponse::from)
//                 .collect(Collectors.toList());
// 
//         return InspectionDashboardResponse.builder()
//                 .character(characterResponse)
//                 .histories(historyResponses)
//                 .build();
//     }
// 
//     /**
//      * 군장검사 캐릭터 설정 수정
//      */
//     public void update(String username, long inspectionCharacterId, UpdateInspectionCharacterRequest request) {
//         InspectionCharacter character = inspectionCharacterRepository
//                 .findByIdAndUsername(inspectionCharacterId, username)
//                 .orElseThrow(() -> new ConditionNotMetException("캐릭터를 찾을 수 없습니다."));
// 
//         character.updateSettings(request.getNoChangeThreshold(), request.isActive());
//     }
// 
//     /**
//      * 군장검사 캐릭터 삭제
//      */
//     public void delete(String username, long inspectionCharacterId) {
//         InspectionCharacter character = inspectionCharacterRepository
//                 .findByIdAndUsername(inspectionCharacterId, username)
//                 .orElseThrow(() -> new ConditionNotMetException("캐릭터를 찾을 수 없습니다."));
// 
//         inspectionCharacterRepository.delete(character);
//     }
// 
//     /**
//      * 수동 새로고침
//      */
//     public InspectionDashboardResponse refresh(String username, long inspectionCharacterId) {
//         Member member = memberService.get(username);
// 
//         if (member.getApiKey() == null || member.getApiKey().isEmpty()) {
//             throw new ConditionNotMetException("API 키가 등록되어 있지 않습니다.");
//         }
// 
//         InspectionCharacter character = inspectionCharacterRepository
//                 .findByIdAndUsername(inspectionCharacterId, username)
//                 .orElseThrow(() -> new ConditionNotMetException("캐릭터를 찾을 수 없습니다."));
// 
//         fetchDailyData(character, member.getApiKey());
// 
//         // 상세 데이터 반환 (최근 30일)
//         LocalDate endDate = LocalDate.now();
//         LocalDate startDate = endDate.minusDays(30);
//         return getDetail(username, inspectionCharacterId, startDate, endDate);
//     }
// 
//     /**
//      * 일일 데이터 수집 (스케줄러 및 수동 새로고침에서 호출)
//      * API 호출은 트랜잭션 밖에서 수행하고, DB 저장만 트랜잭션으로 처리
//      */
//     public void fetchDailyData(InspectionCharacter character, String apiKey) {
//         try {
//             String charName = character.getCharacterName();
// 
//             // 1. 모든 API를 병렬로 조회 (트랜잭션 밖에서 수행)
//             CompletableFuture<CharacterJsonDto> profileFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getCharacterProfileForInspection(charName, apiKey), inspectionExecutor);
//             CompletableFuture<List<ArkgridEffectDto>> effectsFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getArkgridEffects(charName, apiKey), inspectionExecutor);
//             CompletableFuture<List<EquipmentDto>> equipmentFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getEquipment(charName, apiKey), inspectionExecutor);
//             CompletableFuture<List<EngravingDto>> engravingsFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getEngravings(charName, apiKey), inspectionExecutor);
//             CompletableFuture<CardApiResponse> cardsFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getCards(charName, apiKey), inspectionExecutor);
//             CompletableFuture<List<GemDto>> gemsFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getGems(charName, apiKey), inspectionExecutor);
//             CompletableFuture<ArkPassiveApiResponse> arkPassiveFuture = CompletableFuture.supplyAsync(() ->
//                     lostarkCharacterApiClient.getArkPassive(charName, apiKey), inspectionExecutor);
// 
//             CharacterJsonDto profile = profileFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//             List<ArkgridEffectDto> effects = effectsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//             List<EquipmentDto> equipments = equipmentFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//             List<EngravingDto> engravings = engravingsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//             CardApiResponse cardsResponse = cardsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//             List<GemDto> gems = gemsFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//             ArkPassiveApiResponse arkPassiveResponse = arkPassiveFuture.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
// 
//             // 2. DB 저장은 별도 트랜잭션으로 처리 (프록시 통해 호출하여 REQUIRES_NEW 정상 동작)
//             inspectionPersistenceService.saveFetchedData(character, profile, effects, equipments,
//                     engravings, cardsResponse, gems, arkPassiveResponse);
// 
//         } catch (TimeoutException e) {
//             log.error("군장검사 API 타임아웃 - 캐릭터: {}", character.getCharacterName(), e);
//         } catch (Exception e) {
//             log.error("군장검사 데이터 수집 실패 - 캐릭터: {}",
//                     character.getCharacterName(), e);
//         }
//     }
// 
//     /**
//      * 수집 시간 조회
//      */
//     @Transactional(readOnly = true)
//     public int getScheduleHour(String username) {
//         Member member = memberService.get(username);
//         return member.getInspectionScheduleHour();
//     }
// 
//     /**
//      * 수집 시간 변경
//      */
//     public void updateScheduleHour(String username, int scheduleHour) {
//         Member member = memberService.get(username);
//         member.setInspectionScheduleHour(scheduleHour);
//     }
// 
//     /**
//      * 변화량 정보 추가
//      */
//     private void enrichWithChangeInfo(InspectionCharacterResponse response, long inspectionCharacterId) {
//         // 최근 2개 기록만 조회 (limit 2)
//         List<CombatPowerHistory> latest2 = combatPowerHistoryRepository
//                 .findLatest2(inspectionCharacterId);
// 
//         if (latest2.size() >= 2) {
//             CombatPowerHistory latest = latest2.get(0);   // desc 정렬이므로 첫 번째가 최신
//             CombatPowerHistory previous = latest2.get(1);
//             response.setPreviousCombatPower(previous.getCombatPower());
//             response.setCombatPowerChange(latest.getCombatPower() - previous.getCombatPower());
//             response.setPreviousItemLevel(previous.getItemLevel());
//             response.setItemLevelChange(latest.getItemLevel() - previous.getItemLevel());
//         }
// 
//         long unchangedDays = combatPowerHistoryRepository.countConsecutiveUnchangedDays(inspectionCharacterId);
//         response.setUnchangedDays(unchangedDays);
//     }
// }
