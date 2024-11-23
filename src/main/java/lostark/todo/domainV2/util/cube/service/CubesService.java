package lostark.todo.domainV2.util.cube.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.CubeContentDto;
import lostark.todo.domain.content.ContentRepository;
import lostark.todo.domain.content.CubeContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.market.MarketRepository;
import lostark.todo.domainV2.util.cube.dto.CubeResponse;
import lostark.todo.domainV2.util.cube.dto.CubeUpdateRequest;
import lostark.todo.domainV2.util.cube.entity.Cubes;
import lostark.todo.domainV2.util.cube.repository.CubesRepository;
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
            .orElseThrow(() -> new IllegalArgumentException("cube not found"));}

    @Transactional
    public Cubes create(long characterId) {
        Cubes cubes = Cubes.toEntity(characterId);
        return cubeRepository.save(cubes);
    }

    @Transactional
    public Cubes update(CubeUpdateRequest request) {
        Cubes cubes = cubeRepository.findById(request.getCubeId()).orElseThrow(() -> new IllegalArgumentException("cube not found"));
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
                        throw new IllegalArgumentException("해당하는 보석이 없습니다: " + tierName);
                    }

                    return new CubeContentDto().toDto(content, market);
                }).toList();
    }
}
