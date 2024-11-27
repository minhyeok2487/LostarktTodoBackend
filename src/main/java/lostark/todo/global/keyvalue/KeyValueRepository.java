package lostark.todo.global.keyvalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeyValueRepository extends JpaRepository<KeyValue, Long> {

    @Query("SELECT k.keyValue FROM KeyValue k where k.keyName = :keyName")
    String findByKeyName(@Param("keyName") String keyName);

    @Modifying
    @Query("UPDATE KeyValue k SET k.keyValue = " +
            "case when k.keyValue = '1' THEN '2' ELSE '1' END " +
            "where k.keyName = 'two-cycle'")
    int updateTwoCycle();
}
