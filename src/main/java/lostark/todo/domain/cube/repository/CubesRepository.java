package lostark.todo.domain.cube.repository;

import lostark.todo.domain.cube.entity.Cubes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CubesRepository extends JpaRepository<Cubes, Long>, CubesCustomRepository {

}
