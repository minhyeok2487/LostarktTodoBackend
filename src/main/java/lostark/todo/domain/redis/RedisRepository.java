package lostark.todo.domain.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RedisRepository extends CrudRepository<Mail, Long> {

    List<Mail> findAllByMail(String mail);
}
