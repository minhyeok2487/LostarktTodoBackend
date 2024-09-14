package lostark.todo.domainV2.util.cube.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.util.cube.dto.CubeResponse;
import lostark.todo.domainV2.util.cube.repository.CubesRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CubeDao {

    private final CubesRepository cubeRepository;

    @Transactional(readOnly = true)
    public List<CubeResponse> get(String username) {
        return cubeRepository.get(username);
    }
}
