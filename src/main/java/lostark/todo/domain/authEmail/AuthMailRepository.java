package lostark.todo.domain.authEmail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthMailRepository extends JpaRepository<AuthMail, Long> {

    List<AuthMail> findAllByMail(String mail);

    void deleteAllByMail(String mail);

    Optional<AuthMail> findByMailAndNumber(String mail, int number);
}
