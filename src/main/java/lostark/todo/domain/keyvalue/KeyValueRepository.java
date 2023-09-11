package lostark.todo.domain.keyvalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeyValueRepository extends JpaRepository<KeyValue, Long> {

    @Query("SELECT k.keyValue FROM KeyValue k where k.keyName = :keyName")
    String findByKeyName(@Param("keyName") String keyName);
}
