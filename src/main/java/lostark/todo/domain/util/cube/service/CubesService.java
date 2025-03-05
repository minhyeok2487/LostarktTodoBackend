package lostark.todo.domain.util.cube.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.CubeContentDto;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.util.content.repository.ContentRepository;
import lostark.todo.domain.util.content.entity.CubeContent;
import lostark.todo.domain.util.cube.enums.CubeContentName;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.util.market.repository.MarketRepository;
import lostark.todo.domain.util.cube.dto.CubeResponse;
import lostark.todo.domain.util.cube.dto.CubeUpdateRequest;
import lostark.todo.domain.util.cube.entity.Cubes;
import lostark.todo.domain.util.cube.repository.CubesRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CubesService {

    private final ContentRepository contentRepository;
    private final CubesRepository cubeRepository;
    private final MarketRepository marketRepository;

    @Transactional(readOnly = true)
    public List<CubeResponse> get(String username) {
        return cubeRepository.get(username);
    }

    @Transactional
    public Cubes getByCharacterId(Long characterId) {return cubeRepository.getByCharacterId(characterId)
            .orElseThrow(() -> new ConditionNotMetException("cube not found"));}

    @Transactional
    public Cubes create(long characterId) {
        Cubes cubes = Cubes.toEntity(characterId);
        return cubeRepository.save(cubes);
    }

    @Transactional
    public Cubes update(CubeUpdateRequest request) {
        Cubes cubes = cubeRepository.findById(request.getCubeId()).orElseThrow(() -> new ConditionNotMetException("cube not found"));
        return cubes.update(request);
    }

    @Transactional
    public void delete(Long characterId) {
        Cubes cubes = getByCharacterId(characterId);
        cubeRepository.delete(cubes);
    }

    @Transactional(readOnly = true)
    public List<CubeContentDto> getStatistics() {
        Map<String, Market> jewelryMap = marketRepository.findByNameIn(List.of("3티어 1레벨 보석", "4티어 1레벨 보석"))
                .stream().collect(Collectors.toMap(Market::getName, market -> market));
        List<CubeContent> cubeContentList = contentRepository.findAllByCubeContent();
        return cubeContentList.stream()
                .map(content -> {
                    String tierName = content.getLevel() >= 1640 ? "4티어 1레벨 보석" : "3티어 1레벨 보석";
                    Market market = jewelryMap.get(tierName);

                    if (market == null) {
                        throw new ConditionNotMetException("해당하는 보석이 없습니다: " + tierName);
                    }

                    return new CubeContentDto().toDto(content, market);
                }).toList();
    }

    /**
     * 캐릭터의 주간 큐브 티켓을 소모하고, 해당 큐브 컨텐츠에 따른 보석 가격을 반환.
     *
     * @param character       큐브 티켓을 소모할 캐릭터
     * @param cubeContentName 소모할 큐브 컨텐츠 이름
     * @return 소모한 큐브 컨텐츠에 따른 보석의 최근 시장 가격 * 보석 개수
     * @throws IllegalArgumentException 캐릭터, 큐브 티켓, 컨텐츠, 또는 시장 데이터가 없는 경우
     */
    @Transactional
    public double spendWeekCubeTicket(Character character, CubeContentName cubeContentName) {
        // 1. 캐릭터 ID로 Cubes 엔티티 조회 및 검증
        Cubes cubes = getByCharacterId(character.getId());

        // 2. 큐브 티켓 소모
        cubes.spend(cubeContentName);

        // 3. 보석 시장 데이터 조회 및 매핑
        Map<String, Market> jewelryMarketMap = fetchJewelryMarketPrices();

        // 4. 큐브 컨텐츠 조회 및 검증
        CubeContent cubeContent = getCubeContent(cubeContentName);

        // 5. 보석 티어에 따른 시장 가격 계산
        Market market = getMarket(cubeContent, jewelryMarketMap);

        // 6. 최종 가격 계산 (시장 가격 * 보석 개수)
        return calculateJewelryValue(market, cubeContent);
    }

    // 보석 시장 가격 조회 및 매핑
    private Map<String, Market> fetchJewelryMarketPrices() {
        List<String> jewelryNames = List.of("3티어 1레벨 보석", "4티어 1레벨 보석");
        return marketRepository.findByNameIn(jewelryNames)
                .stream()
                .collect(Collectors.toMap(Market::getName, market -> market));
    }

    // 큐브 컨텐츠 조회
    private CubeContent getCubeContent(CubeContentName cubeContentName) {
        return contentRepository.findAllByCubeContent()
                .stream()
                .filter(content -> content.getName().equals(cubeContentName.getName()))
                .findFirst()
                .orElseThrow(() -> new ConditionNotMetException("해당하는 큐브 컨텐츠가 존재하지 않습니다: " + cubeContentName.getName()));
    }

    // 보석 티어에 따른 시장 가격 계산
    private Market getMarket(CubeContent cubeContent, Map<String, Market> jewelryMarketMap) {
        String tierName = determineJewelryTier(cubeContent);
        Market market = jewelryMarketMap.get(tierName);
        if (market == null) {
            throw new ConditionNotMetException("해당 티어의 보석 시장 데이터가 존재하지 않습니다: " + tierName);
        }
        return market;
    }

    // 보석 티어 결정
    private String determineJewelryTier(CubeContent cubeContent) {
        return cubeContent.getLevel() >= 1640 ? "4티어 1레벨 보석" : "3티어 1레벨 보석";
    }

    // 보석 가치 계산
    private double calculateJewelryValue(Market market, CubeContent cubeContent) {
        return market.getRecentPrice() * cubeContent.getJewelry();
    }
}
