package lostark.todo.domain.util.schedule.repository;

import lostark.todo.domain.util.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleCustomRepository {

}
