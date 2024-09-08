package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.cube.CubeResponse;
import lostark.todo.controller.dtoV2.cube.CubeUpdateRequest;
import lostark.todo.domain.cube.Cubes;
import lostark.todo.domain.cube.CubesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CubesService {

    private final CubesRepository cubeRepository;

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
}
