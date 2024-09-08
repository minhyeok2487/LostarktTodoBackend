package lostark.todo.domain.cube;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CubesRepository extends JpaRepository<Cubes, Long>, CubesCustomRepository {

}
