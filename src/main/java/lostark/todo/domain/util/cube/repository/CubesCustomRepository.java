package lostark.todo.domain.util.cube.repository;

import lostark.todo.domain.util.cube.dto.CubeResponse;
import lostark.todo.domain.util.cube.entity.Cubes;

import java.util.List;

public interface CubesCustomRepository {

    List<CubeResponse> get(String username);

    List<Cubes> searchByCharacterId(Long characterId);
}
