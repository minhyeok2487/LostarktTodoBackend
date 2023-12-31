package lostark.todo.domain.redis;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MailRepository extends CrudRepository<Mail, Long> {

    List<Mail> findAllByMail(String mail);

}
