package lostark.todo.domainV2.util.cube.repository;

import lostark.todo.domainV2.util.cube.entity.Cubes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CubesRepository extends JpaRepository<Cubes, Long>, CubesCustomRepository {

}
