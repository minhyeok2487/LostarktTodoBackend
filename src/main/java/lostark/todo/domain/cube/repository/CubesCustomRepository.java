package lostark.todo.domain.cube.repository;

import lostark.todo.domain.cube.dto.CubeResponse;
import lostark.todo.domain.cube.entity.Cubes;

import java.util.List;

public interface CubesCustomRepository {

    List<CubeResponse> get(String username);

    List<Cubes> searchByCharacterId(Long characterId);

    void deleteAllByCharacterId(Long characterId);
}
