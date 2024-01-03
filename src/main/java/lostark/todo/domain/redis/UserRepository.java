package lostark.todo.domain.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
