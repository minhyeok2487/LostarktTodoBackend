package lostark.todo.domain.cube;

import lostark.todo.controller.dtoV2.cube.CubeResponse;

import java.util.List;
import java.util.Optional;

public interface CubesCustomRepository {

    List<CubeResponse> get(String username);

    Optional<Cubes> getByCharacterId(Long characterId);
}
