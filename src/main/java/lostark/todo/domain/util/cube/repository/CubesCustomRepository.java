package lostark.todo.domain.util.cube.repository;

import lostark.todo.domain.util.cube.dto.CubeResponse;
import lostark.todo.domain.util.cube.entity.Cubes;

import java.util.List;
import java.util.Optional;

public interface CubesCustomRepository {

    List<CubeResponse> get(String username);

    Optional<Cubes> getByCharacterId(Long characterId);
}
