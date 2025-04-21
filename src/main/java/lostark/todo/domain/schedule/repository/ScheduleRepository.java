package lostark.todo.domain.schedule.repository;

import lostark.todo.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleCustomRepository {

}
