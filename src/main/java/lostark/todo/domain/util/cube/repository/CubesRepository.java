package lostark.todo.domain.util.cube.repository;

import lostark.todo.domain.util.cube.entity.Cubes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CubesRepository extends JpaRepository<Cubes, Long>, CubesCustomRepository {

}
